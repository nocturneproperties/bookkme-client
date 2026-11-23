package me.bookk.feature.booking.presentation.screen.services

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.Logger
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.loadList
import me.bookk.designsystem.uistate.PickedItem
import me.bookk.designsystem.uistate.decreaseQuantity
import me.bookk.designsystem.uistate.increaseQuantity
import me.bookk.designsystem.uistate.selectedUnits
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import me.bookk.feature.booking.presentation.BookingStateFactory
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.GetBusinessServices
import me.bookk.feature.services.domain.api.service.entity.Service
import org.koin.core.annotation.InjectedParam
import kotlin.time.Duration
import kotlin.time.TimeSource
import kotlin.uuid.Uuid

class BookingServicesViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getBusinesses: GetBusinesses,
    private val getBusinessServices: GetBusinessServices,
    stateFactory: BookingStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    private val openedAt = TimeSource.Monotonic.markNow()
    private val timingLogger = Logger.create("BookingTiming")

    val uiState: BookingServicesState = stateFactory.createBookingServicesState().setup()

    init {
        logTiming("screen opened")
        observeBusinessName()
        observeServices()
        loadServices()
    }

    private fun BookingServicesState.setup() = apply {
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(BookingServicesDestinations.Back)
        }
        groups.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = BookingRes.strings.booking_services_empty.desc(),
        )
        services.isQuantityEditable = true
        services.onQuantityIncreaseRequested = weakVMClosure { vm, option ->
            vm.uiState.services.increaseQuantity(option)
            vm.uiState.updateContinueButton()
        }
        services.onQuantityDecreaseRequested = weakVMClosure { vm, option ->
            vm.uiState.services.decreaseQuantity(option)
            vm.uiState.updateContinueButton()
        }
        continueButton.onClick = weakVMClosure { it.openSpecialist() }
        updateContinueButton()
    }

    private fun observeBusinessName() {
        getBusinesses.flow(setOf(businessId))
            .map { businesses -> businesses.firstOrNull()?.name }
            .distinctUntilChanged()
            .flowOn(DispatcherProvider.io)
            .safeOnEach {
                logTiming("business name from database: $it")
                renderBusinessName(it)
            }
            .observe()
    }

    private fun observeServices() {
        getBusinessServices.flow(businessId)
            .flowOn(DispatcherProvider.io)
            .safeOnEach {
                logTiming("services from database: ${it.size}")
                renderServices(it)
            }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun loadServices() {
        loadList(
            listState = uiState.groups,
            notifications = uiState.notifications,
            call = { refreshBusinessAndServices() },
        )
    }

    private suspend fun refreshBusinessAndServices(): List<Service> {
        return coroutineScope {
            val business = async {
                getBusinesses.refresh(setOf(businessId)).also { logTiming("business refresh done") }
            }
            val services = async {
                getBusinessServices.refresh(businessId).also { logTiming("services refresh done") }
            }
            business.await()
            services.await().filter { it.isAvailable }
        }
    }

    private fun logTiming(event: String) {
        timingLogger.i("+${openedAt.elapsedNow().inWholeMilliseconds}ms $event")
    }

    private fun renderBusinessName(name: String?) {
        uiState.businessName = name?.desc()
        uiState.businessInitials = name.orEmpty().initials()
    }

    private fun String.initials(): String {
        return split(" ")
            .filter { it.isNotBlank() }
            .take(MAX_INITIALS)
            .joinToString("") { it.first().uppercase() }
    }

    private fun renderServices(services: List<Service>) {
        val groups = services.bookableGroups()
        if (groups.isEmpty() && uiState.groups.isInitialLoading) return
        val options = groups.flatMap { it.services }
        uiState.services.replaceOptions(options)
        uiState.services.replaceSelected(stillOffered(options))
        uiState.groups.replace(groups)
        uiState.updateContinueButton()
    }

    private fun List<Service>.bookableGroups(): List<ServiceGroupItem> {
        return filter { it.isAvailable }
            .groupBy { it.group }
            .entries
            .sortedBy { it.key.createdAt }
            .map { (group, services) -> groupItem(group, services) }
    }

    private fun groupItem(group: ServiceGroup, services: List<Service>): ServiceGroupItem {
        return ServiceGroupItem(
            id = group.id.toString(),
            name = group.name.desc(),
            services = services.map(::serviceOption),
        )
    }

    private fun serviceOption(service: Service): ServiceOption {
        return ServiceOption(
            pickerItemId = service.id.toString(),
            displayName = service.name.desc(),
            details = BookingRes.strings.booking_services_details.format(
                service.duration.text(),
                service.price.toString(),
            ),
            service = service,
        )
    }

    private fun stillOffered(options: List<ServiceOption>): List<PickedItem<ServiceOption>> {
        val optionsById = options.associateBy { it.pickerItemId }
        return uiState.services.selectedItems.mapNotNull { picked ->
            optionsById[picked.item.pickerItemId]?.let {
                uiState.services.createPickedItem(it, picked.quantity)
            }
        }
    }

    private fun BookingServicesState.updateContinueButton() {
        val picked = services.selectedUnits().map { it.service }
        continueButton.isEnabled = picked.isNotEmpty()
        continueButton.text = continueText(picked)
    }

    private fun continueText(picked: List<Service>): StringDesc {
        if (picked.isEmpty()) {
            return BookingRes.strings.booking_services_pick.desc()
        }
        val duration = picked.fold(Duration.ZERO) { total, service -> total + service.duration }
        val price = picked.map { it.price }.reduce { total, price -> total + price }
        return BookingRes.strings.booking_services_continue.format(
            duration.text(),
            price.toString(),
        )
    }

    private fun Duration.text(): StringDesc {
        val hours = inWholeHours.toInt()
        val minutes = (inWholeMinutes % MINUTES_IN_HOUR).toInt()
        return when {
            hours == 0 -> BookingRes.strings.booking_duration_minutes.format(minutes)
            minutes == 0 -> BookingRes.strings.booking_duration_hours.format(hours)
            else -> BookingRes.strings.booking_duration_hours_minutes.format(hours, minutes)
        }
    }

    private fun openSpecialist() {
        val services = uiState.services.selectedItems.map { picked ->
            RequestedService(picked.item.service.id, picked.quantity)
        }
        uiState.navigation.push(BookingServicesDestinations.Specialist(businessId, services))
    }

    private companion object {
        const val MAX_INITIALS = 2
        const val MINUTES_IN_HOUR = 60
    }
}
