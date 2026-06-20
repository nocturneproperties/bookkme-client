package me.bookk.feature.appointments.presentation.screen.settings

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.appointments.domain.api.UpdateAppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class AppointmentSettingsViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getAppointmentSettings: GetAppointmentSettings,
    private val updateAppointmentSettings: UpdateAppointmentSettings,
    dateLocalizer: DateLocalizer,
    stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    private val dateFormat = dateLocalizer.forStyle(DateStyle.SHORT)
    private var loadedSettings: AppointmentSettings = AppointmentSettings(businessId)

    val uiState: AppointmentSettingsState = stateFactory.createAppointmentSettingsState().setup()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getAppointmentSettings(businessId) },
            onComplete = ::renderSettings,
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun renderSettings(settings: AppointmentSettings) {

    }

    private fun onSaveClick() {

        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.save.startLoading() },
            call = { updateAppointmentSettings(TODO()) },
            onComplete = { loadedSettings = it },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { uiState.save.stopLoading() }
        )
    }

    private fun AppointmentSettingsState.setup() = apply {
        appBar.size = TopBarSize.SMALL
        appBar.title = AppointmentsRes.strings.appointments_settings_title.desc()
        appBar.onBackClick = weakSelfClosure { it.uiState.navigation.push(AppointmentSettingsDestination.Back) }

        save.text = DesignSystem.strings.action_save.desc()
        save.onClick = weakSelfClosure { it.onSaveClick() }
    }
}
