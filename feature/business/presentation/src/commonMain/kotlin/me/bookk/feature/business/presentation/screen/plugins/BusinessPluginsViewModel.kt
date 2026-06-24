package me.bookk.feature.business.presentation.screen.plugins

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.Action
import me.bookk.designsystem.uistate.simple.OptionalInfoLine
import me.bookk.designsystem.uistate.simple.optionalLine
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.business.domain.api.plugin.EnableAppointmentsPlugin
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.screen.plugins.BusinessPluginsDestinations.Back
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class BusinessPluginsViewModel(
    @InjectedParam private val businessId: Uuid,
    private val isAppointmentsPluginEnabled: IsAppointmentsPluginEnabled,
    private val enableAppointmentsPlugin: EnableAppointmentsPlugin,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: BusinessPluginListState = stateFactory.createBusinessPluginListState().setup()

    init {
        initPluginsState()
    }

    private fun initPluginsState() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.appointmentPlugin.enable.startLoading() },
            call = { isAppointmentsPluginEnabled(businessId) },
            onComplete = { uiState.appointmentPlugin.isEnabled = it },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { uiState.appointmentPlugin.enable.stopLoading() }
        )
    }

    private fun enableAppointmentsPlugin() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.appointmentPlugin.enable.startLoading() },
            call = { enableAppointmentsPlugin(businessId) },
            onComplete = { uiState.appointmentPlugin.isEnabled = true },
            onError = {
                when (it) {
                    is EnableAppointmentsPlugin.Error.AlreadyEnabled -> {
                        uiState.appointmentPlugin.isEnabled = true
                    }

                    else -> uiState.notifications.add(it.notification())
                }
            },
            onTerminate = { uiState.appointmentPlugin.enable.stopLoading() }
        )
    }

    private fun onAppointmentDemoClick() {

    }

    private fun BusinessPluginListState.setup() = apply {
        appBar.size = TopBarSize.SMALL
        appBar.title = BusinessRes.strings.business_plugins_title.desc()
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(Back) }
        with(appointmentPlugin) {
            title = BusinessRes.strings.business_plugins_appointments_title.desc()
            subtitle = BusinessRes.strings.business_plugins_appointments_subtitle.desc()

            youCan.replace(
                listOf(
                    BusinessRes.strings.business_plugins_appointments_you_item1.desc()
                        .optionalLine(),
                    BusinessRes.strings.business_plugins_appointments_you_item2.desc()
                        .optionalLine(),
                    BusinessRes.strings.business_plugins_appointments_you_item3.desc()
                        .optionalLine(),
                    BusinessRes.strings.business_plugins_appointments_you_item4.desc()
                        .optionalLine(),
                    BusinessRes.strings.business_plugins_appointments_you_item5.desc()
                        .optionalLine(),
                    BusinessRes.strings.business_plugins_appointments_you_item6.desc()
                        .optionalLine()
                )
            )

            clientCan.replace(
                listOf(
                    OptionalInfoLine(
                        BusinessRes.strings.business_plugins_appointments_clients_item1.desc(),
                        BusinessRes.strings.business_plugins_appointments_clients_item1_sub.desc(),
                    ),
                    OptionalInfoLine(
                        BusinessRes.strings.business_plugins_appointments_clients_item2.desc(),
                        BusinessRes.strings.business_plugins_appointments_clients_item2_sub.desc(),
                    ),
                )
            )

            demo = Action(
                BusinessRes.strings.business_plugins_demo.desc(),
                onClick = weakVMClosure { it.onAppointmentDemoClick() }
            )
            enable.text = DesignSystem.strings.action_enable.desc()
            enable.onClick = weakVMClosure { it.enableAppointmentsPlugin() }
        }
    }
}