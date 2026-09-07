package me.bookk.feature.appointments.presentation.screen.settings

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.appointments.domain.api.UpdateAppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

/**
 * Working schedule and day offs are owned by the business and edited on the business settings
 * screen — this screen only covers the appointment-specific preferences.
 */
class AppointmentSettingsViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getAppointmentSettings: GetAppointmentSettings,
    private val updateAppointmentSettings: UpdateAppointmentSettings,
    stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    private var loadedSettings = AppointmentSettings.stub(businessId = businessId)

    val uiState: AppointmentSettingsState = stateFactory.createAppointmentSettingsState().setup()

    init {
        observeSettings()
        loadSettings()
    }

    private fun observeSettings() {
        getAppointmentSettings.flow(businessId)
            .flowOn(DispatcherProvider.io)
            .onEach { settings ->
                settings?.let {
                    loadedSettings = it
                    renderSettings(it)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadSettings() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getAppointmentSettings.refresh(businessId) },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun onSaveClick() {
        val appointmentSettings = snapshotState()
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.save.startLoading() },
            call = { updateAppointmentSettings(appointmentSettings) },
            onComplete = {
                loadedSettings = it
                renderSettings(it)
                uiState.notifications.add(
                    PresentationNotification.GlobalMessage(
                        AppointmentsRes.strings.appointments_settings_updated.desc()
                    )
                )
            },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { uiState.save.stopLoading() }
        )
    }

    private fun snapshotState(): AppointmentSettings = loadedSettings.copy(
        automaticApproval = uiState.automaticApproval.isChecked,
        inBetweenBreakInMinutes = uiState.minimalBreak.text.toIntOrNull() ?: DEFAULT_BREAK_MINUTES,
        appointmentNote = uiState.note.text
    )

    private fun renderSettings(settings: AppointmentSettings) = with(uiState) {
        automaticApproval.isChecked = settings.automaticApproval
        note.text = settings.appointmentNote
        minimalBreak.text = settings.inBetweenBreakInMinutes.toString()
    }

    private fun AppointmentSettingsState.setup() = apply {
        setupAppBar()
        setupAutomaticApproval()
        setupMinimalBreak()
        setupNote()
        setupSaveButton()
    }

    private fun AppointmentSettingsState.setupSaveButton() {
        save.text = DesignSystem.strings.action_save.desc()
        save.onClick = weakVMClosure { it.onSaveClick() }
    }

    private fun AppointmentSettingsState.setupNote() {
        note.placeholder = AppointmentsRes.strings.appointments_settings_note_placeholder.desc()
        note.maxLength = NOTE_MAX_LENGTH
        note.onTextChanged = weakSelfClosure { state, v -> state.note.text = v }
    }

    private fun AppointmentSettingsState.setupAutomaticApproval() {
        automaticApproval.text =
            AppointmentsRes.strings.appointments_settings_automatic_approval.desc()
        automaticApproval.onCheckedChange = weakVMClosure { vm, v ->
            vm.uiState.automaticApproval.isChecked = v
        }
    }

    private fun AppointmentSettingsState.setupAppBar() {
        appBar.size = TopBarSize.SMALL
        appBar.title = AppointmentsRes.strings.appointments_settings_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(AppointmentSettingsDestination.Back)
        }
    }

    private fun AppointmentSettingsState.setupMinimalBreak() {
        minimalBreak.label = AppointmentsRes.strings.appointments_settings_minimal_break.desc()
        minimalBreak.suffix = DesignSystem.strings.common_min.desc()
        minimalBreak.inputType = InputType.DIGIT
        minimalBreak.onTextChanged = weakSelfClosure { state, v ->
            state.minimalBreak.text = v.filter { it.isDigit() }
        }
        minimalBreak.supportingTextRes = AppointmentsRes.strings.appointments_settings_break_footer.desc()
    }

    private companion object {
        const val DEFAULT_BREAK_MINUTES = 10
        const val NOTE_MAX_LENGTH = 2048
    }
}
