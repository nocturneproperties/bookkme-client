package me.bookk.feature.booking.presentation.screen.services

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.loadList
import me.bookk.designsystem.resources.toStringDesc
import me.bookk.designsystem.uistate.PickedItem
import me.bookk.designsystem.uistate.decreaseQuantity
import me.bookk.designsystem.uistate.increaseQuantity
import me.bookk.designsystem.uistate.selectedUnits
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.booking.domain.api.GetBookingCatalog
import me.bookk.feature.booking.domain.api.GetBookingPreparation
import me.bookk.feature.booking.domain.api.SetBookingServices
import me.bookk.feature.booking.domain.api.entity.BookingCatalog
import me.bookk.feature.booking.domain.api.entity.BookingPreparation
import me.bookk.feature.booking.domain.api.entity.BookingPreparation.PreparedService
import me.bookk.feature.booking.domain.api.entity.BookingService
import me.bookk.feature.booking.domain.api.entity.BookingServiceGroup
import me.bookk.feature.booking.presentation.BookingStateFactory
import me.bookk.feature.booking.presentation.screen.totalDuration
import me.bookk.feature.booking.presentation.screen.totalPrice
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class BookingServicesViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getBookingCatalog: GetBookingCatalog,
    private val getBookingPreparation: GetBookingPreparation,
    private val setBookingServices: SetBookingServices,
    stateFactory: BookingStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: BookingServicesState = stateFactory.createBookingServicesState().setup()

    init {
        observeCatalog()
        restoreSavedServices()
        loadCatalog()
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
        continueButton.onClick = weakVMClosure { it.saveServicesAndContinue() }
        updateContinueButton()
    }

    private fun observeCatalog() {
        getBookingCatalog.flow(businessId)
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderCatalog(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun restoreSavedServices() {
        getBookingPreparation.flow(businessId)
            .take(1)
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderSavedServices(it) }
            .observe()
    }

    private fun loadCatalog() {
        loadList(
            listState = uiState.groups,
            notifications = uiState.notifications,
            call = { getBookingCatalog.refresh(businessId).groups },
        )
    }

    private fun saveServicesAndContinue() {
        val services = uiState.services.selectedItems.map {
            PreparedService(it.item.service, it.quantity)
        }
        launch(
            key = SAVE_SERVICES_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            call = { setBookingServices(businessId, services) },
            onComplete = {
                uiState.navigation.push(BookingServicesDestinations.Specialist(businessId))
            },
            onError = { uiState.notifications.add(it.notification()) },
        )
    }

    private fun renderCatalog(catalog: BookingCatalog) {
        uiState.businessName = catalog.businessName?.desc()
        if (catalog.groups.isEmpty() && uiState.groups.isInitialLoading) return
        val groups = catalog.groups.map(::groupItem)
        val options = groups.flatMap { it.services }
        uiState.services.replaceOptions(options)
        uiState.services.replaceSelected(stillOffered(options))
        uiState.groups.replace(groups)
        uiState.updateContinueButton()
    }

    private fun renderSavedServices(preparation: BookingPreparation) {
        uiState.services.replaceSelected(
            preparation.services.map {
                uiState.services.createPickedItem(serviceOption(it.service), it.quantity)
            },
        )
        uiState.updateContinueButton()
    }

    private fun groupItem(group: BookingServiceGroup): ServiceGroupItem {
        return ServiceGroupItem(
            id = group.id.toString(),
            name = group.name.desc(),
            services = group.services.map(::serviceOption),
        )
    }

    private fun serviceOption(service: BookingService): ServiceOption {
        return ServiceOption(
            pickerItemId = service.id.toString(),
            displayName = service.name.desc(),
            details = BookingRes.strings.booking_services_details.format(
                service.duration.toStringDesc(),
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
        total = servicesTotal(picked)
        continueButton.isEnabled = picked.isNotEmpty()
        continueButton.text = continueText(picked)
    }

    private fun servicesTotal(picked: List<BookingService>): ServicesTotal? {
        if (picked.isEmpty()) return null
        return ServicesTotal(
            summary = BookingRes.strings.booking_services_summary.format(
                BookingRes.plurals.booking_services_count.format(picked.size, picked.size),
                picked.totalDuration().toStringDesc(),
            ),
            price = picked.totalPrice().toString().desc(),
        )
    }

    private fun continueText(picked: List<BookingService>): StringDesc {
        if (picked.isEmpty()) {
            return BookingRes.strings.booking_services_pick.desc()
        }
        return DesignSystem.strings.action_continue.desc()
    }

    private companion object {
        const val SAVE_SERVICES_KEY = "saveServices"
    }
}
