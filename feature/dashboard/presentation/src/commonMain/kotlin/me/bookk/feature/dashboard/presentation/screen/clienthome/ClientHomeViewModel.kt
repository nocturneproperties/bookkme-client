package me.bookk.feature.dashboard.presentation.screen.clienthome

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.dashboard.resources.DashboardRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.domain.launch.ConfirmableAction
import me.bookk.core.domain.launch.LaunchParameter
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.loadList
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.appointments.domain.api.GetSavedBusinesses
import me.bookk.feature.appointments.domain.api.GetUpcomingBookings
import me.bookk.feature.appointments.domain.api.SaveBusiness
import me.bookk.feature.appointments.domain.api.entity.SavedBusiness
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.appointments.domain.api.entity.UpcomingBooking
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.business.domain.api.link.ResolveBusinessLink
import me.bookk.feature.dashboard.presentation.ClientDashboardStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.time.Duration
import kotlin.uuid.Uuid

class ClientHomeViewModel(
    @InjectedParam private val startParameter: String,
    private val getUpcomingBookings: GetUpcomingBookings,
    private val getSavedBusinesses: GetSavedBusinesses,
    private val saveBusiness: SaveBusiness,
    private val resolveBusinessLink: ResolveBusinessLink,
    private val userProfileCRUD: UserProfileCRUD,
    stateFactory: ClientDashboardStateFactory,
    dateLocalizer: DateLocalizer,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: ClientHomeState = stateFactory.createClientHomeState().setup()

    private val dateFormatter = dateLocalizer.strict(DATE_PATTERN, respectUserSettings = true)
    private val timeFormatter = dateLocalizer.forStyle(DateStyle.SHORT)
    private val visitFormatter = dateLocalizer.forStyle(DateStyle.D_MMM_YYYY_RELATIVE)

    init {
        observeFirstName()
        observeUpcomingBookings()
        observeSavedBusinesses()
        loadUpcomingBookings()
        openLaunchParameterOrLoadBusinesses()
    }

    private fun ClientHomeState.setup() = apply {
        settingsButton.text = DashboardRes.strings.dashboard_client_home_settings.desc()
        settingsButton.onClick = weakVMClosure {
            it.uiState.navigation.push(ClientHomeDestinations.Settings)
        }
        allBookingsButton.text = DashboardRes.strings.dashboard_client_home_more.desc()
        allBookingsButton.endIcon = DesignSystem.images.material_chevron_right
        allBookingsButton.onClick = weakVMClosure {
            it.uiState.navigation.push(ClientHomeDestinations.Bookings)
        }
        upcoming.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = DashboardRes.strings.dashboard_client_home_no_upcoming.desc(),
        )
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
            date = dateFormatter.format(booking.date.date),
            time = timeAndSpecialist(booking),
            businessName = booking.businessName,
            services = servicesText(booking.services),
            status = booking.status.label(),
            statusColor = booking.status.color(),
            onClick = weakVMClosure { it.uiState.navigation.push(booking.detailsDestination()) },
        )
    }

    private fun timeAndSpecialist(booking: UpcomingBooking): StringDesc {
        return DashboardRes.strings.dashboard_client_home_time_and_specialist.format(
            timeFormatter.format(booking.date.time),
            timeFormatter.format(booking.endTime()),
            booking.employee.fullName,
        )
    }

    private fun UpcomingBooking.endTime(): LocalTime {
        val duration = services.fold(Duration.ZERO) { total, service -> total + service.duration }
        val end = date.toInstant(TimeZone.UTC) + duration
        return end.toLocalDateTime(TimeZone.UTC).time
    }

    private fun servicesText(services: List<ServiceSnapshot>): String {
        return services.groupBy { it.id }.values.joinToString(", ") { sameServices ->
            val name = sameServices.first().name
            if (sameServices.size == 1) name else "$name × ${sameServices.size}"
        }
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

    private fun UpcomingBooking.detailsDestination(): ClientHomeDestinations {
        return when (status) {
            UpcomingBooking.Status.WAITING_FOR_CONFIRMATION -> {
                ClientHomeDestinations.RequestDetails(id)
            }

            UpcomingBooking.Status.CONFIRMED -> {
                ClientHomeDestinations.AppointmentDetails(id)
            }
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
            name = business.name,
            initials = initialsOf(business.name),
            subtitle = savedBusinessSubtitle(business),
            onClick = weakVMClosure {
                it.uiState.navigation.push(ClientHomeDestinations.Services(business.id))
            },
        )
    }

    private fun initialsOf(name: String): String {
        return name.split(' ')
            .filter { it.isNotBlank() }
            .take(BUSINESS_INITIALS_COUNT)
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

    private fun openLaunchParameterOrLoadBusinesses() {
        when (val launch = LaunchParameter.parse(startParameter)) {
            is LaunchParameter.Business -> openLaunchBusiness(launch.id)
            is LaunchParameter.BusinessLink -> openLaunchLink(launch.name)
            is LaunchParameter.Confirmation -> openLaunchConfirmation(launch.action)
            LaunchParameter.UnsupportedOperation -> explainUnsupportedLaunch()
            LaunchParameter.None -> loadSavedBusinesses()
        }
    }

    private fun openLaunchBusiness(businessId: Uuid) {
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

    private fun openLaunchLink(name: String) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { resolveBusinessLink(name) },
            onComplete = { openLaunchBusiness(it.businessId) },
            onError = {
                uiState.notifications.add(resolveErrorNotification(it))
                loadSavedBusinesses()
            },
        )
    }

    private fun resolveErrorNotification(error: Throwable): PresentationNotification {
        if (error !is ResolveBusinessLink.Error.NotFound) return error.notification()
        return PresentationNotification.Message.simple(
            DashboardRes.strings.dashboard_client_home_link_not_found.desc(),
        )
    }

    private fun openLaunchConfirmation(action: ConfirmableAction) {
        uiState.navigation.push(ClientHomeDestinations.ConfirmAction(action))
        loadSavedBusinesses()
    }

    private fun explainUnsupportedLaunch() {
        uiState.notifications.add(
            PresentationNotification.Message.simple(
                DashboardRes.strings.dashboard_client_home_link_unsupported.desc(),
            ),
        )
        loadSavedBusinesses()
    }

    private fun loadSavedBusinesses() {
        loadList(
            listState = uiState.businesses,
            notifications = uiState.notifications,
            call = { getSavedBusinesses.refresh() },
        )
    }

    private companion object {
        const val DATE_PATTERN = "EEE, d MMM"
        const val BUSINESS_INITIALS_COUNT = 2
    }
}
