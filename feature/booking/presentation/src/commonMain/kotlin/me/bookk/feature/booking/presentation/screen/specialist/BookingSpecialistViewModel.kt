package me.bookk.feature.booking.presentation.screen.specialist

import dev.icerock.moko.resources.desc.Composition
import dev.icerock.moko.resources.desc.StringDesc
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
import me.bookk.designsystem.resources.toStringDesc
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.GetBookingSpecialists
import me.bookk.feature.booking.domain.api.SetBookingSpecialist
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.booking.domain.api.entity.BookingSpecialist
import me.bookk.feature.booking.presentation.BookingStateFactory
import me.bookk.feature.booking.presentation.screen.totalDuration
import me.bookk.feature.booking.presentation.screen.totalPrice
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class BookingSpecialistViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getBookingPreparation: GetBookingPreparation,
    private val getBookingSpecialists: GetBookingSpecialists,
    private val setBookingSpecialist: SetBookingSpecialist,
    stateFactory: BookingStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: BookingSpecialistState = stateFactory.createBookingSpecialistState().setup()

    init {
        observeSummary()
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
    }

    private fun observeSummary() {
        getBookingPreparation.flow(businessId)
            .flowOn(DispatcherProvider.io)
            .safeOnEach { uiState.summary = summary(it.services) }
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

    private fun saveSpecialistAndContinue(specialist: BookingSpecialist) {
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

    private fun renderSpecialists(specialists: List<BookingSpecialist>) {
        if (specialists.isEmpty() && uiState.specialists.isInitialLoading) return
        uiState.specialists.replace(specialists.map(::specialistItem))
    }

    private fun specialistItem(specialist: BookingSpecialist): SpecialistItem {
        return SpecialistItem(
            id = specialist.id.toString(),
            name = specialist.fullName().desc(),
            initials = specialist.initials(),
            onClick = weakVMClosure { it.saveSpecialistAndContinue(specialist) },
        )
    }

    private fun summary(services: List<PreparedService>): StringDesc? {
        if (services.isEmpty()) return null
        val units = services.flatMap { prepared -> List(prepared.quantity) { prepared.service } }
        return BookingRes.strings.booking_specialist_summary.format(
            StringDesc.Composition(services.map(::serviceName), SERVICE_SEPARATOR),
            units.totalDuration().toStringDesc(),
            units.totalPrice().toString(),
        )
    }

    private fun serviceName(prepared: PreparedService): StringDesc {
        if (prepared.quantity == 1) return prepared.service.name.desc()
        return BookingRes.strings.booking_specialist_service_count.format(
            prepared.service.name,
            prepared.quantity,
        )
    }

    private fun BookingSpecialist.fullName(): String {
        return listOf(name, lastName).filter { it.isNotBlank() }.joinToString(" ")
    }

    private fun BookingSpecialist.initials(): String {
        return listOf(name, lastName)
            .mapNotNull { it.trim().firstOrNull()?.uppercaseChar() }
            .joinToString("")
    }

    private companion object {
        const val SAVE_SPECIALIST_KEY = "saveSpecialist"
        const val SERVICE_SEPARATOR = ", "
    }
}
