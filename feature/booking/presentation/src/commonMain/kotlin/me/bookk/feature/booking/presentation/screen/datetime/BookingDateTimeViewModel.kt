package me.bookk.feature.booking.presentation.screen.datetime

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.startOfWeek
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.simple.ErrorState
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.GetBookingSlots
import me.bookk.feature.booking.domain.api.GetBookingTimetable
import me.bookk.feature.booking.domain.api.SetBookingSlot
import me.bookk.feature.booking.domain.api.entity.BookingDay
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import me.bookk.feature.booking.domain.api.entity.BookingSlot
import me.bookk.feature.booking.domain.api.entity.BookingTimetable
import me.bookk.feature.booking.presentation.BookingStateFactory
import me.bookk.feature.booking.presentation.screen.fullName
import org.koin.core.annotation.InjectedParam
import kotlin.time.Clock
import kotlin.uuid.Uuid

class BookingDateTimeViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getBookingTimetable: GetBookingTimetable,
    private val getBookingPreparation: GetBookingPreparation,
    private val getBookingSlots: GetBookingSlots,
    private val setBookingSlot: SetBookingSlot,
    private val clock: Clock,
    dateLocalizer: DateLocalizer,
    stateFactory: BookingStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    private val weekStrip = BookingWeekStrip(dateLocalizer)
    private val slotGroups = BookingSlotGroups(dateLocalizer)
    private val slotTotal = BookingSlotTotal(dateLocalizer)

    private var timetable: BookingTimetable? = null
    private var preparation: BookingPreparation? = null
    private var week: Map<LocalDate, BookingDay> = emptyMap()

    val uiState: BookingDateTimeState = stateFactory.createBookingDateTimeState().setup()

    init {
        observeBooking()
        refreshTimetable()
    }

    private fun BookingDateTimeState.setup() = apply {
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(BookingDateTimeDestinations.Back)
        }
        pickDateButton.text = BookingRes.strings.booking_date_time_pick_date.desc()
        pickDateButton.onClick = weakVMClosure { it.uiState.datePicker.isDatePickerVisible = true }
        datePicker.onDatePicked = weakVMClosure { viewModel, date: LocalDate ->
            viewModel.showDate(date)
        }
        continueButton.onClick = weakVMClosure { it.saveSlotAndContinue() }
        renderContinueButton(null)
    }

    private fun observeBooking() {
        combine(
            getBookingTimetable.flow(businessId),
            getBookingPreparation.flow(businessId),
            ::Pair,
        )
            .flowOn(DispatcherProvider.io)
            .safeOnEach { (timetable, preparation) -> renderBooking(timetable, preparation) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun refreshTimetable() {
        launch(
            key = REFRESH_TIMETABLE_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { if (timetable == null) showSlotsLoading() },
            call = { getBookingTimetable.refresh(businessId) },
            onError = { showTimetableError(it) },
        )
    }

    private fun showTimetableError(error: Throwable) {
        when {
            error is GetBookingTimetable.Error.BusinessNotFound -> leaveBooking()
            timetable == null -> showSlotsError(error) { it.refreshTimetable() }
            else -> handleError(error)
        }
    }

    private fun renderBooking(timetable: BookingTimetable, preparation: BookingPreparation) {
        this.timetable = timetable
        this.preparation = preparation
        uiState.businessName = timetable.businessName.desc()
        uiState.specialistName = timetable.specialist?.fullName()?.desc()
        showDate(uiState.datePicker.pickedDate ?: openingDate(timetable))
    }

    private fun showDate(date: LocalDate) {
        if (timetable == null) return
        val shownWeekStart = uiState.datePicker.pickedDate?.startOfWeek()
        uiState.datePicker.pickedDate = date
        if (date.startOfWeek() != shownWeekStart) loadWeek(date.startOfWeek())
        renderPickedDate()
    }

    private fun loadWeek(weekStart: LocalDate) {
        val timetable = timetable ?: return
        val dates = weekStart.weekDates()
        launch(
            key = LOAD_WEEK_KEY,
            launchBehaviour = LaunchBehaviour.DropOldest,
            launchIn = DispatcherProvider.io,
            onStart = {
                week = emptyMap()
                showSlotsLoading()
            },
            call = { getBookingSlots(businessId, timetable, dates.first(), dates.last()) },
            onComplete = { showWeek(it) },
            onError = { showWeekError(it, weekStart) },
        )
    }

    private fun showSlotsLoading() {
        uiState.slots.clear()
        uiState.slots.errorState = null
        uiState.slots.isInitialLoading = true
    }

    private fun showWeek(days: List<BookingDay>) {
        week = days.associateBy { it.date }
        uiState.slots.isInitialLoading = false
        leaveFullOpeningDate()
        renderPickedDate()
    }

    private fun showWeekError(error: Throwable, weekStart: LocalDate) {
        when (error) {
            is GetBookingSlots.Error.SelectionInvalid -> leaveBooking()
            is GetBookingSlots.Error.BookingUnavailable -> showBookingUnavailable()
            else -> showSlotsError(error) { it.loadWeek(weekStart) }
        }
    }

    private fun showBookingUnavailable() {
        uiState.slots.isInitialLoading = false
        uiState.slots.emptyState = bookingUnavailableState(businessName())
        uiState.slots.clear()
    }

    private fun showSlotsError(error: Throwable, retry: (BookingDateTimeViewModel) -> Unit) {
        val description = error.description()
        uiState.slots.isInitialLoading = false
        uiState.slots.errorState = ErrorState(
            title = description.title,
            subtitle = description.message,
            onRetryClick = weakVMClosure { retry(it) },
        )
    }

    private fun leaveBooking() {
        uiState.navigation.push(BookingDateTimeDestinations.Leave)
    }

    private fun leaveFullOpeningDate() {
        val timetable = timetable ?: return
        val picked = uiState.datePicker.pickedDate
        if (picked != openingDate(timetable) || week[picked]?.slots.orEmpty().isNotEmpty()) return
        week.firstFreeDayFrom(today(timetable))?.let { uiState.datePicker.pickedDate = it }
    }

    private fun renderPickedDate() {
        val timetable = timetable ?: return
        val date = uiState.datePicker.pickedDate ?: return
        renderWeekStrip(date, today(timetable))
        when (val day = week[date]) {
            null -> renderSelection(date, null)
            else -> renderSlots(day, selectedSlot(day))
        }
    }

    private fun renderWeekStrip(date: LocalDate, today: LocalDate) {
        uiState.monthTitle = weekStrip.monthTitle(date)
        uiState.datePicker.minDate = today
        uiState.days = weekStrip.days(
            picked = date,
            today = today,
            week = week,
            onDayClick = weakVMClosure { viewModel, day: LocalDate -> viewModel.showDate(day) },
        )
    }

    private fun renderSlots(day: BookingDay, selected: BookingSlot?) {
        uiState.slots.emptyState = day.noSlotsState(businessName())
        uiState.slots.replace(
            slotGroups.groups(
                slots = day.slots,
                selected = selected,
                onSlotClick = weakVMClosure { viewModel, slot: BookingSlot ->
                    viewModel.selectSlot(slot)
                },
            ),
        )
        renderSelection(day.date, selected)
    }

    private fun selectSlot(slot: BookingSlot) {
        val day = pickedDay() ?: return
        renderSlots(day, slot)
    }

    private fun renderSelection(date: LocalDate, slot: BookingSlot?) {
        uiState.renderContinueButton(slot)
        uiState.total = slotTotal.total(preparation?.services.orEmpty(), date, slot)
    }

    private fun BookingDateTimeState.renderContinueButton(slot: BookingSlot?) {
        continueButton.isEnabled = slot != null
        continueButton.text = when (slot) {
            null -> BookingRes.strings.booking_date_time_pick_time.desc()
            else -> DesignSystem.strings.action_continue.desc()
        }
    }

    private fun saveSlotAndContinue() {
        val slot = pickedDay()?.let { selectedSlot(it) } ?: return
        launch(
            key = SAVE_SLOT_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            call = { setBookingSlot(businessId, slot.start) },
            onComplete = {
                uiState.navigation.push(BookingDateTimeDestinations.Review(businessId))
            },
            onError = { uiState.notifications.add(it.notification()) },
        )
    }

    private fun selectedSlot(day: BookingDay): BookingSlot? {
        return day.slots.shownAsSelectedIn(uiState.slots)
            ?: day.slots.firstOrNull { it.start == preparation?.slotStart }
    }

    private fun pickedDay(): BookingDay? {
        return week[uiState.datePicker.pickedDate]
    }

    private fun openingDate(timetable: BookingTimetable): LocalDate {
        val today = today(timetable)
        val approvedDate = preparation?.slotStart?.toLocalDateTime(timetable.timeZone)?.date
        return approvedDate?.takeIf { it >= today } ?: today
    }

    private fun today(timetable: BookingTimetable): LocalDate {
        return clock.now().toLocalDateTime(timetable.timeZone).date
    }

    private fun businessName(): String {
        return timetable?.businessName.orEmpty()
    }

    private companion object {
        const val REFRESH_TIMETABLE_KEY = "refreshTimetable"
        const val LOAD_WEEK_KEY = "loadWeek"
        const val SAVE_SLOT_KEY = "saveSlot"
    }
}
