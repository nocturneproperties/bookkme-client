package me.bookk.feature.dashboard.presentation.screen.clienthome

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.android.feature.dashboard.resources.DashboardRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.loadList
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.simple
import me.bookk.feature.appointments.domain.api.GetSavedBusinesses
import me.bookk.feature.appointments.domain.api.GetUpcomingBookings
import me.bookk.feature.appointments.domain.api.SaveBusiness
import me.bookk.feature.appointments.domain.api.entity.SavedBusiness
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.appointments.domain.api.entity.UpcomingBooking
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.dashboard.presentation.ClientDashboardStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class ClientHomeViewModel(
    @InjectedParam private val startParameter: String,
    private val getUpcomingBookings: GetUpcomingBookings,
    private val getSavedBusinesses: GetSavedBusinesses,
    private val saveBusiness: SaveBusiness,
    private val userProfileCRUD: UserProfileCRUD,
    stateFactory: ClientDashboardStateFactory,
    dateLocalizer: DateLocalizer,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: ClientHomeState = stateFactory.createClientHomeState().setup()

    private val weekdayFormatter = dateLocalizer.strict(WEEKDAY_PATTERN, respectUserSettings = true)
    private val timeFormatter = dateLocalizer.forStyle(DateStyle.SHORT)
    private val visitFormatter = dateLocalizer.forStyle(DateStyle.D_MMM_YYYY_RELATIVE)

    init {
        observeFirstName()
        observeUpcomingBookings()
        observeSavedBusinesses()
        loadUpcomingBookings()
        openLaunchBusinessOrLoadBusinesses()
    }

    private fun ClientHomeState.setup() = apply {
        settingsButton.text = DashboardRes.strings.dashboard_client_home_settings.desc()
        settingsButton.onClick = weakVMClosure {
            it.uiState.navigation.push(ClientHomeDestinations.Settings)
        }
        allBookingsButton.text =
            DashboardRes.strings.dashboard_client_home_all_bookings.desc()
        allBookingsButton.onClick = weakVMClosure {
            it.uiState.navigation.push(ClientHomeDestinations.Bookings)
        }
        allBusinessesButton.text =
            DashboardRes.strings.dashboard_client_home_all_businesses.desc()
        allBusinessesButton.onClick = weakVMClosure {
            it.uiState.navigation.push(ClientHomeDestinations.Businesses)
        }
    }

    private fun observeFirstName() {
        userProfileCRUD.observe()
            .map { it?.firstName }
            .distinctUntilChanged()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { uiState.greeting = greeting(it) }
            .observe()
    }

    private fun greeting(firstName: String?): StringDesc {
        if (firstName.isNullOrBlank()) {
            return DashboardRes.strings.dashboard_client_home_greeting_default.desc()
        }
        return DashboardRes.strings.dashboard_client_home_greeting.format(firstName)
    }

    private fun observeUpcomingBookings() {
        getUpcomingBookings.flow()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderUpcomingBookings(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderUpcomingBookings(bookings: List<UpcomingBooking>) {
        if (bookings.isEmpty() && uiState.upcoming.isInitialLoading) return
        uiState.upcoming.replace(bookings.map(::upcomingBookingItem))
    }

    private fun upcomingBookingItem(booking: UpcomingBooking): UpcomingBookingItem {
        return UpcomingBookingItem(
            id = booking.id.toString(),
            weekday = weekdayFormatter.format(booking.date.date),
            day = booking.date.day.toString(),
            businessName = booking.businessName,
            services = servicesText(booking.services),
            timeAndSpecialist = timeAndSpecialist(booking),
            status = booking.status.label(),
            statusColor = booking.status.color(),
            onClick = weakVMClosure {
                it.uiState.navigation.push(ClientHomeDestinations.BookingDetails(booking.id))
            },
        )
    }

    private fun timeAndSpecialist(booking: UpcomingBooking): StringDesc {
        val time = timeFormatter.format(booking.date.time)
        val format = DashboardRes.strings.dashboard_client_home_time_with_specialist
        return format.format(time, booking.employee.fullName)
    }

    private fun servicesText(services: List<ServiceSnapshot>): String {
        return services.groupBy { it.id }.values.joinToString(", ") { sameServices ->
            val name = sameServices.first().name
            if (sameServices.size == 1) name else "$name × ${sameServices.size}"
        }
    }

    private fun observeSavedBusinesses() {
        getSavedBusinesses.flow()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderSavedBusinesses(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderSavedBusinesses(businesses: List<SavedBusiness>) {
        if (businesses.isEmpty() && uiState.businesses.isInitialLoading) return
        uiState.businesses.replace(businesses.map(::savedBusinessItem))
    }

    private fun savedBusinessItem(business: SavedBusiness): SavedBusinessItem {
        return SavedBusinessItem(
            id = business.id.toString(),
            initials = initials(business.name),
            name = business.name,
            subtitle = savedBusinessSubtitle(business),
            onClick = weakVMClosure {
                it.uiState.navigation.push(ClientHomeDestinations.Services(business.id))
            },
        )
    }

    private fun initials(name: String): String {
        return name.split(' ')
            .filter { it.isNotBlank() }
            .take(MAX_INITIALS)
            .joinToString("") { it.first().uppercase() }
    }

    private fun savedBusinessSubtitle(business: SavedBusiness): StringDesc {
        val lastVisitAt = business.lastVisitAt
        return when {
            !business.isEnabled -> {
                DashboardRes.strings.dashboard_client_home_not_taking_bookings.desc()
            }

            lastVisitAt == null -> {
                DashboardRes.strings.dashboard_client_home_no_visits.desc()
            }

            else -> {
                val visitDate = lastVisitAt.toLocalDateTime(TimeZone.currentSystemDefault()).date
                DashboardRes.strings.dashboard_client_home_last_visit.format(
                    visitFormatter.format(visitDate),
                )
            }
        }
    }

    private fun loadUpcomingBookings() {
        loadList(
            listState = uiState.upcoming,
            notifications = uiState.notifications,
            call = { getUpcomingBookings.refresh() },
        )
    }

    private fun loadSavedBusinesses() {
        loadList(
            listState = uiState.businesses,
            notifications = uiState.notifications,
            call = { getSavedBusinesses.refresh() },
        )
    }

    private fun openLaunchBusinessOrLoadBusinesses() {
        val businessId = Uuid.parseOrNull(startParameter)
        if (businessId == null) {
            loadSavedBusinesses()
            return
        }
        uiState.navigation.push(ClientHomeDestinations.Services(businessId))
        saveLaunchBusiness(businessId)
    }

    private fun saveLaunchBusiness(businessId: Uuid) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { saveBusiness(businessId) },
            onError = {
                uiState.notifications.add(saveErrorNotification(it))
                loadSavedBusinesses()
            },
        )
    }

    private fun saveErrorNotification(error: Throwable): PresentationNotification {
        val message = when (error as? SaveBusiness.Error) {
            is SaveBusiness.Error.AppointmentsNotEnabled -> {
                DashboardRes.strings.dashboard_client_home_save_not_enabled.desc()
            }

            is SaveBusiness.Error.SavedBusinessesLimitReached -> {
                DashboardRes.strings.dashboard_client_home_save_limit.desc()
            }

            null -> {
                return error.notification()
            }
        }
        return PresentationNotification.Message.simple(message)
    }

    private fun UpcomingBooking.Status.label(): StringDesc {
        return when (this) {
            UpcomingBooking.Status.CONFIRMED -> {
                DashboardRes.strings.dashboard_client_home_status_confirmed.desc()
            }

            UpcomingBooking.Status.WAITING_FOR_CONFIRMATION -> {
                DashboardRes.strings.dashboard_client_home_status_waiting.desc()
            }
        }
    }

    private fun UpcomingBooking.Status.color(): ColorToken {
        return when (this) {
            UpcomingBooking.Status.CONFIRMED -> ColorToken.Success
            UpcomingBooking.Status.WAITING_FOR_CONFIRMATION -> ColorToken.SecondaryText
        }
    }

    private companion object {
        const val WEEKDAY_PATTERN = "EEE"
        const val MAX_INITIALS = 2
    }
}
