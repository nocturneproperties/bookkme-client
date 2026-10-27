package me.bookk.feature.dashboard.presentation

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.flowOn
import me.bookk.android.feature.dashboard.resources.DashboardRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.simple
import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.domain.api.business.JoinBusiness
import me.bookk.feature.business.domain.api.business.ObserveDashboardSetupStatus
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.business.SwitchDashboardBusiness
import me.bookk.feature.business.domain.api.entity.DashboardSetupStatus
import me.bookk.feature.dashboard.presentation.state.DashboardState
import me.bookk.feature.dashboard.presentation.state.HomeContent
import me.bookk.feature.dashboard.presentation.state.OnboardingBusinessItem
import me.bookk.feature.dashboard.presentation.state.TabItem
import me.bookk.feature.dashboard.presentation.state.TabItemsState
import kotlin.uuid.Uuid

class DashboardViewModel(
    private val observeDashboardSetupStatus: ObserveDashboardSetupStatus,
    private val refreshBusinessInfo: RefreshBusinessInfo,
    private val observeUserBusinessesChanges: ObserveUserBusinessesChanges,
    private val switchDashboardBusiness: SwitchDashboardBusiness,
    private val joinBusiness: JoinBusiness,
    private val createBusiness: CreateBusiness,
    stateFactory: DashboardStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: DashboardState = stateFactory.createDashboardState(createInitData()).setup()

    private var setupRequiredBusinessId: Uuid? = null
    private var lastSetupStatus: DashboardSetupStatus? = null

    init {
        requestSilentInformationRefresh()
        observeSetupStatus()
        observeUserBusinesses()
    }

    private fun requestSilentInformationRefresh() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { refreshBusinessInfo() },
            onComplete = {},
            onError = {}
        )
    }

    private fun DashboardState.setup() = apply {
        home.onboarding.onCreateBusinessClick = weakVMClosure { it.showBusinessNameDialog() }
        home.onboarding.onJoinBusinessClick = weakVMClosure { it.onJoinBusinessClick() }
        home.onboarding.onEnablePluginsClick = weakVMClosure { it.onEnablePluginsClick() }
        home.onboarding.onBusinessClick = weakVMClosure { vm, item -> vm.onBusinessSelected(item) }
    }

    private fun observeUserBusinesses() {
        observeUserBusinessesChanges()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { businesses ->
                uiState.home.onboarding.businesses = businesses.map { OnboardingBusinessItem(it.id, it.name) }
            }
            .observe()
    }

    private fun onBusinessSelected(item: OnboardingBusinessItem) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { switchDashboardBusiness(item.id) },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun onEnablePluginsClick() {
        setupRequiredBusinessId?.let {
            uiState.navigation.push(DashboardHomeNavigationDestination.EnablePlugins(it))
        }
    }

    private fun showBusinessNameDialog() {
        uiState.notifications.add(
            PresentationNotification.InputMessage(
                title = DashboardRes.strings.dashboard_create_dialog_title.desc(),
                message = DashboardRes.strings.dashboard_create_dialog_message.desc(),
                placeholder = DashboardRes.strings.dashboard_create_dialog_placeholder.desc(),
                cancelText = DesignSystem.strings.action_cancel.desc(),
                confirmText = DesignSystem.strings.action_create.desc(),
                onConfirm = weakVMClosure { vm, name -> vm.onBusinessNameSubmit(name) }
            )
        )
    }

    private fun onBusinessNameSubmit(name: String) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { createBusiness(name) },
            onComplete = {},
            onError = {
                when (it) {
                    is CreateBusiness.Error.EmptyName -> uiState.notifications.add(
                        PresentationNotification.Message.simple(DashboardRes.strings.dashboard_create_error_empty_name.desc())
                    )

                    else -> uiState.notifications.add(it.notification())
                }
            }
        )
    }

    private fun onJoinBusinessClick() {
        uiState.notifications.add(
            PresentationNotification.InputMessage(
                title = DashboardRes.strings.dashboard_join_dialog_title.desc(),
                message = DashboardRes.strings.dashboard_join_dialog_message.desc(),
                placeholder = DashboardRes.strings.dashboard_join_dialog_placeholder.desc(),
                cancelText = DesignSystem.strings.action_cancel.desc(),
                confirmText = DashboardRes.strings.dashboard_join_dialog_confirm.desc(),
                onConfirm = weakVMClosure { vm, code -> vm.onJoinSubmit(code) }
            )
        )
    }

    private fun onJoinSubmit(code: String) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { joinBusiness(code) },
            onComplete = {
                uiState.notifications.add(
                    PresentationNotification.GlobalMessage(
                        text = DashboardRes.strings.dashboard_join_success.desc(),
                        state = PresentationNotification.GlobalMessage.State.SUCCESS
                    )
                )
            },
            onError = {
                when (it) {
                    is JoinBusiness.Error.EmptyCode -> uiState.notifications.add(
                        PresentationNotification.Message.simple(DashboardRes.strings.dashboard_join_error_empty_code.desc())
                    )

                    is JoinBusiness.Error.AlreadyProcessed -> uiState.notifications.add(
                        PresentationNotification.Message.simple(DashboardRes.strings.dashboard_join_error_already_processed.desc())
                    )

                    is JoinBusiness.Error.EmployeeExists -> uiState.notifications.add(
                        PresentationNotification.Message.simple(DashboardRes.strings.dashboard_join_error_employee_exists.desc())
                    )

                    else -> uiState.notifications.add(it.notification())
                }
            }
        )
    }

    private fun observeSetupStatus() {
        observeDashboardSetupStatus()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderSetupStatus(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderSetupStatus(status: DashboardSetupStatus) {
        if (isDashboardBusinessLost(status)) {
            uiState.tabItems.selectedItemId = TabItem.Id.HOME
        }
        lastSetupStatus = status
        setupRequiredBusinessId = (status as? DashboardSetupStatus.SetupRequired)?.businessId
        uiState.tabItems.items.first { it.id == TabItem.Id.BUSINESS }.isEnabled = status != DashboardSetupStatus.NoBusiness
        if (status is DashboardSetupStatus.AwaitingSetup) {
            uiState.home.onboarding.awaitingSetupMessage =
                DashboardRes.strings.dashboard_onboarding_awaiting_message.format(status.businessName)
        }
        uiState.home.content = when (status) {
            DashboardSetupStatus.NoBusiness -> HomeContent.NoBusiness
            is DashboardSetupStatus.SetupRequired -> HomeContent.SetupRequired
            is DashboardSetupStatus.AwaitingSetup -> HomeContent.AwaitingSetup
            DashboardSetupStatus.Ready -> HomeContent.ActivePlugin
        }
    }

    private fun isDashboardBusinessLost(status: DashboardSetupStatus): Boolean {
        val previous = lastSetupStatus
        return status == DashboardSetupStatus.NoBusiness && previous != null && previous != DashboardSetupStatus.NoBusiness
    }

    companion object {
        fun createInitData() = TabItemsState.InitData(
            selectedItemId = TabItem.Id.HOME,
            tabInitData = listOf(
                TabItem.InitData(
                    id = TabItem.Id.HOME,
                    text = DashboardRes.strings.dashboard_item_home.desc()
                ),
                TabItem.InitData(
                    id = TabItem.Id.BUSINESS,
                    text = DashboardRes.strings.dashboard_item_business.desc(),
                    isEnabled = false
                ),
                TabItem.InitData(
                    id = TabItem.Id.SETTINGS,
                    text = DashboardRes.strings.dashboard_item_settings.desc()
                )
            )
        )
    }
}
