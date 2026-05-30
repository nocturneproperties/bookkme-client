package me.bookk.feature.services.presentation.service.add

import dev.icerock.moko.resources.desc.desc
import library.money.api.CurrencyFactory
import library.money.api.Money
import library.money.api.Money.SupportedCurrency
import me.bookk.android.feature.services.resources.ServicesRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.services.domain.api.GetBusinessCurrency
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.service.CreateService
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.presentation.ServicesStateFactory
import me.bookk.feature.services.presentation.service.add.AddServiceDestination.Back
import kotlin.properties.Delegates
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

class AddServiceViewModel(
    private val businessId: Uuid,
    private val createService: CreateService,
    private val getServiceGroups: GetServiceGroups,
    private val getBusinessCurrency: GetBusinessCurrency,
    stateFactory: ServicesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: AddServiceState = stateFactory.createServiceState()
    private var currency: SupportedCurrency by Delegates.notNull()

    init {
        loadCurrency()
        loadGroups()
    }

    private fun loadCurrency() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getBusinessCurrency(businessId) },
            onComplete = {
                currency = it
                uiState.setup(it)
            },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun loadGroups() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getServiceGroups(businessId) },
            onComplete = {
                uiState.group.replaceOptions(it.map(AddServiceState::GroupUI))
            },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun onNameChanged(text: String) {
        uiState.name.text = text
        uiState.name.isValid = text.isNotBlank()
        invalidateButton()
    }

    private fun onDurationChanged(text: String) {
        uiState.duration.text = text.filter { it.isDigit() }
        uiState.duration.isValid = uiState.duration.text.isNotEmpty()
        invalidateButton()
    }

    private fun onGroupSelected(group: AddServiceState.GroupUI) {
        uiState.group.textField.updateText(group.displayName)
        uiState.group.selectedItem = group
        uiState.group.textField.isValid = true
        invalidateButton()
    }

    private fun onPriceChanged(price: String) {
        val symbols = listOf('.', ',')
        uiState.price.text = price
            .filter { it.isDigit() || it in symbols }
            .replace(",", ".")
        uiState.price.isValid = uiState.price.text.toFloatOrNull() != null
        invalidateButton()
    }

    private fun onEnabledChanged(enabled: Boolean) {
        uiState.enabled.isChecked = enabled
    }

    private fun onCreateClick() {
        val group = uiState.group.selectedItem ?: return
        val service = Service(
            id = Uuid.random(),
            businessId = businessId,
            group = group.domain,
            name = uiState.name.text,
            duration = uiState.duration.text.toInt().seconds,
            price = Money(uiState.price.text.toDouble(), currency),
            isAvailable = uiState.enabled.isChecked,
            createdAt = Clock.System.now()
        )
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.create.startLoading() },
            call = { createService(service) },
            onComplete = { uiState.navigation.push(Back) },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { uiState.create.stopLoading() }
        )
    }

    private fun invalidateButton() {
        uiState.create.isEnabled = uiState.name.isValid &&
                uiState.group.selectedItem != null &&
                uiState.price.isValid &&
                uiState.duration.isValid
    }

    private fun AddServiceState.setup(currency: SupportedCurrency) = apply {
        appBar.title = ServicesRes.strings.services_create_title.desc()
        appBar.onBackClick = weakSelfClosure { it.uiState.navigation.push(Back) }

        name.label = ServicesRes.strings.services_create_name.desc()
        name.onTextChanged = weakSelfClosure { vm, name -> vm.onNameChanged(name) }
        name.isValid = false
        name.inputType = InputType.TEXT
        name.placeholder = ServicesRes.strings.services_create_name_placeholder.desc()

        group.textField.label = ServicesRes.strings.services_create_group.desc()
        group.onItemPicked = weakSelfClosure { vm, item -> item?.let { vm.onGroupSelected(it) } }
        group.textField.isValid = false
        group.textField.placeholder = ServicesRes.strings.services_create_group_placeholder.desc()
        group.pickerType = PickerFieldState.PickerType.SCREEN
        group.pickerTitle = ServicesRes.strings.services_group_pick.desc()

        duration.label = ServicesRes.strings.services_create_duration.desc()
        duration.placeholder = ServicesRes.strings.services_create_duration_placeholder.desc()
        duration.suffix = ServicesRes.strings.services_create_min.desc()
        duration.onTextChanged = weakSelfClosure { vm, duration -> vm.onDurationChanged(duration) }
        duration.isValid = false
        duration.inputType = InputType.DIGIT

        price.label = ServicesRes.strings.services_create_price.desc()
        price.placeholder = ServicesRes.strings.services_create_price_placeholder.desc()
        price.onTextChanged = weakSelfClosure { vm, price -> vm.onPriceChanged(price) }
        price.suffix = CurrencyFactory.forCode(currency.code).symbol().desc()
        price.isValid = false
        price.inputType = InputType.DECIMAL

        enabled.text = ServicesRes.strings.services_create_visible.desc()
        enabled.onCheckedChange = weakSelfClosure { vm, enabled -> vm.onEnabledChanged(enabled) }

        create.text = DesignSystem.strings.action_create.desc()
        create.onClick = weakSelfClosure { it.onCreateClick() }
        create.isEnabled = false
    }
}