package me.bookk.feature.services.presentation.service.edit

import dev.icerock.moko.resources.desc.desc
import library.money.api.Money
import me.bookk.android.feature.services.resources.ServicesRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.deleteConfirmation
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.services.domain.api.GetServicesPermissions
import me.bookk.feature.services.domain.api.entity.ServicesPermissions
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.service.DeleteService
import me.bookk.feature.services.domain.api.service.EditService
import me.bookk.feature.services.domain.api.service.GetService
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.presentation.ServicesStateFactory
import me.bookk.feature.services.presentation.service.ServiceGroupOption
import me.bookk.feature.services.presentation.service.edit.EditServiceDestination.Back
import me.bookk.feature.services.presentation.service.toDurationInput
import me.bookk.feature.services.presentation.service.toPriceInput
import org.koin.core.annotation.InjectedParam
import kotlin.properties.Delegates.notNull
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class EditServiceViewModel(
    @InjectedParam private val serviceId: Uuid,
    private val getService: GetService,
    private val editService: EditService,
    private val deleteService: DeleteService,
    private val getServiceGroups: GetServiceGroups,
    private val getServicesPermissions: GetServicesPermissions,
    stateFactory: ServicesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: EditServiceState = stateFactory.createEditServiceState().setup()
    private var service: Service by notNull()

    init {
        loadService()
    }

    private fun loadService() {
        launch(
            launchIn = DispatcherProvider.io,
            call = {
                val service = getService(serviceId)
                service to getServicesPermissions(service.businessId)
            },
            onComplete = { (service, permissions) ->
                renderService(service)
                renderPermissions(permissions)
                loadGroups(service.businessId)
            },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun loadGroups(businessId: Uuid) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getServiceGroups.refresh(businessId) },
            onComplete = { uiState.group.replaceOptions(it.map(::ServiceGroupOption)) },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun renderService(service: Service) {
        this.service = service
        onGroupSelected(ServiceGroupOption(service.group))
        onNameChanged(service.name)
        onDurationChanged(service.duration.inWholeMinutes.toString())
        onPriceChanged(service.price.valueToStringWithoutAmountSeparation())
        uiState.price.suffix = service.price.currency.symbol().desc()
        onAvailableChanged(service.isAvailable)
    }

    private fun renderPermissions(permissions: ServicesPermissions) {
        uiState.group.textField.enabled = permissions.canEdit
        uiState.name.enabled = permissions.canEdit
        uiState.duration.enabled = permissions.canEdit
        uiState.price.enabled = permissions.canEdit
        uiState.available.isEnabled = permissions.canEdit
        uiState.submit.isVisible = permissions.canEdit
        uiState.deleteButton.isVisible = permissions.canDelete
    }

    private fun onGroupSelected(group: ServiceGroupOption) {
        uiState.group.textField.updateText(group.displayName)
        uiState.group.selectedItem = group
        uiState.group.textField.isValid = true
        invalidateButton()
    }

    private fun onNameChanged(text: String) {
        uiState.name.text = text
        uiState.name.isValid = text.isNotBlank()
        invalidateButton()
    }

    private fun onDurationChanged(text: String) {
        uiState.duration.text = text.toDurationInput()
        uiState.duration.isValid = uiState.duration.text.isNotEmpty()
        invalidateButton()
    }

    private fun onPriceChanged(text: String) {
        uiState.price.text = text.toPriceInput()
        uiState.price.isValid = uiState.price.text.toDoubleOrNull() != null
        invalidateButton()
    }

    private fun onAvailableChanged(available: Boolean) {
        uiState.available.isChecked = available
    }

    private fun onSubmit() {
        val group = uiState.group.selectedItem ?: return
        val edited = service.copy(
            group = group.domain,
            name = uiState.name.text,
            duration = uiState.duration.text.toInt().minutes,
            price = Money(uiState.price.text.toDouble(), service.price.currencyType),
            isAvailable = uiState.available.isChecked
        )
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.submit.startLoading() },
            call = { editService(edited) },
            onComplete = {
                uiState.notifications.add(PresentationNotification.SuccessHaptic)
                uiState.navigation.push(Back)
            },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = {
                uiState.submit.stopLoading()
                invalidateButton()
            }
        )
    }

    private fun onDeleteClicked() {
        uiState.notifications.add(
            PresentationNotification.Message.deleteConfirmation(
                onConfirmed = weakVMClosure { it.onDeleteConfirmed() }
            )
        )
    }

    private fun onDeleteConfirmed() {
        val service = service
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.deleteButton.startLoading() },
            call = { deleteService(service) },
            onComplete = { uiState.navigation.push(Back) },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { uiState.deleteButton.stopLoading() }
        )
    }

    private fun invalidateButton() {
        uiState.submit.isEnabled = uiState.group.selectedItem != null &&
                uiState.name.isValid &&
                uiState.duration.isValid &&
                uiState.price.isValid
    }

    private fun EditServiceState.setup() = apply {
        appBar.size = TopBarSize.SMALL
        appBar.title = ServicesRes.strings.services_edit_title.desc()
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(Back) }

        group.textField.label = ServicesRes.strings.services_create_group.desc()
        group.textField.placeholder = ServicesRes.strings.services_create_group_placeholder.desc()
        group.textField.isValid = false
        group.pickerType = PickerFieldState.PickerType.SCREEN
        group.pickerTitle = ServicesRes.strings.services_group_pick.desc()
        group.onItemPicked = weakVMClosure { vm, item -> item?.let { vm.onGroupSelected(it) } }

        name.label = ServicesRes.strings.services_create_name.desc()
        name.placeholder = ServicesRes.strings.services_create_name_placeholder.desc()
        name.inputType = InputType.TEXT
        name.isValid = false
        name.onTextChanged = weakVMClosure { vm, text -> vm.onNameChanged(text) }

        duration.label = ServicesRes.strings.services_create_duration.desc()
        duration.placeholder = ServicesRes.strings.services_create_duration_placeholder.desc()
        duration.suffix = DesignSystem.strings.common_min.desc()
        duration.inputType = InputType.DIGIT
        duration.isValid = false
        duration.onTextChanged = weakVMClosure { vm, text -> vm.onDurationChanged(text) }

        price.label = ServicesRes.strings.services_create_price.desc()
        price.placeholder = ServicesRes.strings.services_create_price_placeholder.desc()
        price.inputType = InputType.DECIMAL
        price.isValid = false
        price.onTextChanged = weakVMClosure { vm, text -> vm.onPriceChanged(text) }

        available.text = ServicesRes.strings.services_create_visible.desc()
        available.onCheckedChange = weakVMClosure { vm, checked -> vm.onAvailableChanged(checked) }

        submit.isEnabled = false
        submit.text = DesignSystem.strings.action_save.desc()
        submit.onClick = weakVMClosure { it.onSubmit() }

        deleteButton.text = ServicesRes.strings.services_delete_button.desc()
        deleteButton.isVisible = false
        deleteButton.onClick = weakVMClosure { it.onDeleteClicked() }
    }
}
