package me.bookk.feature.booking.presentation.screen.specialist

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.flowOn
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.loadList
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.booking.domain.api.GetBookingCatalog
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.GetBookingSpecialists
import me.bookk.feature.booking.domain.api.SetBookingSpecialist
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import me.bookk.feature.booking.domain.api.entity.BookingSpecialist
import me.bookk.feature.booking.presentation.BookingStateFactory
import me.bookk.feature.booking.presentation.screen.bookingTotal
import me.bookk.feature.booking.presentation.screen.units
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class BookingSpecialistViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getBookingCatalog: GetBookingCatalog,
    private val getBookingPreparation: GetBookingPreparation,
    private val getBookingSpecialists: GetBookingSpecialists,
    private val setBookingSpecialist: SetBookingSpecialist,
    stateFactory: BookingStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    private var specialists: List<BookingSpecialist>? = null
    private var approvedSpecialistId: Uuid? = null
    private var tappedSpecialistId: Uuid? = null

    val uiState: BookingSpecialistState = stateFactory.createBookingSpecialistState().setup()

    init {
        observeBusinessName()
        observePreparation()
        observeSpecialists()
        loadSpecialists()
    }

    private fun BookingSpecialistState.setup() = apply {
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(BookingSpecialistDestinations.Back)
        }
        specialists.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = BookingRes.strings.booking_specialist_empty.desc(),
        )
        continueButton.onClick = weakVMClosure { it.saveSpecialistAndContinue() }
        renderContinueButton(null)
    }

    private fun observeBusinessName() {
        getBookingCatalog.flow(businessId)
            .flowOn(DispatcherProvider.io)
            .safeOnEach { uiState.businessName = it.businessName?.desc() }
            .observe()
    }

    private fun observePreparation() {
        getBookingPreparation.flow(businessId)
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderPreparation(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun observeSpecialists() {
        getBookingSpecialists.flow(businessId)
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderSpecialists(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun loadSpecialists() {
        loadList(
            listState = uiState.specialists,
            notifications = uiState.notifications,
            call = { getBookingSpecialists.refresh(businessId) },
        )
    }

    private fun saveSpecialistAndContinue() {
        val specialist = selectedSpecialist() ?: return
        launch(
            key = SAVE_SPECIALIST_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            call = { setBookingSpecialist(businessId, specialist.id) },
            onComplete = {
                uiState.navigation.push(BookingSpecialistDestinations.DateTime(businessId))
            },
            onError = { uiState.notifications.add(it.notification()) },
        )
    }

    private fun renderPreparation(preparation: BookingPreparation) {
        uiState.total = preparation.services.units().bookingTotal()
        approvedSpecialistId = preparation.specialistId
        renderSelection()
    }

    private fun renderSpecialists(specialists: List<BookingSpecialist>) {
        if (specialists.isEmpty() && uiState.specialists.isInitialLoading) return
        this.specialists = specialists
        renderSelection()
    }

    private fun selectSpecialist(specialist: BookingSpecialist) {
        tappedSpecialistId = specialist.id
        renderSelection()
    }

    private fun renderSelection() {
        val specialists = specialists ?: return
        val selected = selectedSpecialist()
        uiState.specialists.replace(specialists.map { specialistItem(it, it == selected) })
        uiState.renderContinueButton(selected)
    }

    private fun BookingSpecialistState.renderContinueButton(selected: BookingSpecialist?) {
        continueButton.isEnabled = selected != null
        continueButton.text = selected
            ?.let { BookingRes.strings.booking_specialist_continue.format(it.name.trim()) }
            ?: DesignSystem.strings.action_continue.desc()
    }

    private fun selectedSpecialist(): BookingSpecialist? {
        return listed(tappedSpecialistId) ?: listed(approvedSpecialistId)
            ?: specialists?.firstOrNull()
    }

    private fun listed(id: Uuid?): BookingSpecialist? {
        return specialists?.firstOrNull { it.id == id }
    }

    private fun specialistItem(specialist: BookingSpecialist, isSelected: Boolean): SpecialistItem {
        return SpecialistItem(
            id = specialist.id.toString(),
            name = specialist.fullName().desc(),
            initials = specialist.initials(),
            isSelected = isSelected,
            onClick = weakVMClosure { it.selectSpecialist(specialist) },
        )
    }

    private fun BookingSpecialist.fullName(): String {
        return listOf(name, lastName)
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .joinToString(" ")
    }

    private fun BookingSpecialist.initials(): String {
        return listOf(name, lastName)
            .mapNotNull { it.trim().firstOrNull()?.uppercaseChar() }
            .joinToString("")
    }

    private companion object {
        const val SAVE_SPECIALIST_KEY = "saveSpecialist"
    }
}
