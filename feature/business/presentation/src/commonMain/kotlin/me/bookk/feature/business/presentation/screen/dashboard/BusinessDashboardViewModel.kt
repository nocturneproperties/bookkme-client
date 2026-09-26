package me.bookk.feature.business.presentation.screen.dashboard

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.BusinessMenuItem
import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.domain.api.business.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.business.JoinBusiness
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.business.domain.api.business.SwitchDashboardBusiness
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardSection
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardState
import kotlin.uuid.Uuid

class BusinessDashboardViewModel(
    private val getAvailableDashboardFeatures: GetAvailableDashboardFeatures,
    private val observeUserBusinessesChanges: ObserveUserBusinessesChanges,
    private val switchDashboardBusiness: SwitchDashboardBusiness,
    private val joinBusiness: JoinBusiness,
    private val createBusiness: CreateBusiness,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: BusinessDashboardState = stateFactory.createBusinessDashboardState().setup()
    private var businessId = Uuid.random()

    init {
        observeDashboard()
        observeUserBusinesses()
    }

    private fun BusinessDashboardState.setup() = apply {
        businessMenu.onBusinessClick = weakVMClosure { vm, id -> vm.onBusinessMenuClick(id) }
        businessMenu.onCreateClick = weakVMClosure { it.onCreateClick() }
        businessMenu.onJoinClick = weakVMClosure { it.onJoinClick() }
    }

    private fun observeDashboard() {
        getAvailableDashboardFeatures()
            .filterNotNull()
            .distinctUntilChanged()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { overview ->
                uiState.appBar.title = overview.business.name.desc()
                uiState.businessMenu.selectedBusinessId = overview.business.id
                businessId = overview.business.id
                applyFeatures(overview.business.id, overview.features)
            }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun observeUserBusinesses() {
        observeUserBusinessesChanges()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { businesses ->
                uiState.businessMenu.items = businesses.map { BusinessMenuItem(it.id, it.name) }
            }
            .observe()
    }

    private fun applyFeatures(businessId: Uuid, features: Set<DashboardFeature>) {
        val sectionList = buildList {
            if (features.contains(DashboardFeature.BUSINESS)) {
                add(BusinessDashboardSection.Business(businessId, features))
            }
            if (features.contains(DashboardFeature.APPOINTMENTS)) {
                add(BusinessDashboardSection.Appointments(businessId))
            }
            if (features.contains(DashboardFeature.SHOP)) {
                add(BusinessDashboardSection.Shop())
            }
        }
        uiState.updateSections(sectionList)
    }

    private fun onBusinessMenuClick(id: Uuid) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { switchDashboardBusiness(id) },
            onComplete = {},
            onError = {}
        )
    }

    private fun onCreateClick() {
        uiState.notifications.add(
            PresentationNotification.InputMessage(
                title = BusinessRes.strings.create_business_title.desc(),
                message = BusinessRes.strings.create_business_name_supporting.desc(),
                placeholder = BusinessRes.strings.create_business_name_hint.desc(),
                cancelText = DesignSystem.strings.action_cancel.desc(),
                confirmText = DesignSystem.strings.action_create.desc(),
                onConfirm = weakVMClosure { vm, name -> vm.onCreateSubmit(name) }
            )
        )
    }

    private fun onCreateSubmit(name: String) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { createBusiness(name) },
            onComplete = {},
            onError = {
                when (it) {
                    is CreateBusiness.Error.EmptyName -> uiState.notifications.add(
                        PresentationNotification.Message.simple(BusinessRes.strings.create_business_error_empty_name.desc())
                    )

                    else -> uiState.notifications.add(it.notification())
                }
            }
        )
    }

    private fun onJoinClick() {
        uiState.notifications.add(
            PresentationNotification.InputMessage(
                title = BusinessRes.strings.business_dashboard_switch_join_dialog_title.desc(),
                message = BusinessRes.strings.business_dashboard_switch_join_dialog_message.desc(),
                placeholder = BusinessRes.strings.business_dashboard_switch_join_dialog_placeholder.desc(),
                cancelText = DesignSystem.strings.action_cancel.desc(),
                confirmText = BusinessRes.strings.business_dashboard_switch_join.desc(),
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
                        text = BusinessRes.strings.business_dashboard_switch_join_success.desc(),
                        state = PresentationNotification.GlobalMessage.State.SUCCESS
                    )
                )
            },
            onError = {
                when (it) {
                    is JoinBusiness.Error.EmptyCode ->
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                BusinessRes.strings.business_dashboard_switch_join_error_empty_code.desc()
                            )
                        )

                    is JoinBusiness.Error.AlreadyProcessed ->
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                BusinessRes.strings.business_dashboard_switch_join_error_already_processed.desc()
                            )
                        )

                    is JoinBusiness.Error.EmployeeExists ->
                        uiState.notifications.add(
                            PresentationNotification.Message.simple(
                                BusinessRes.strings.business_dashboard_switch_join_error_employee_exists.desc()
                            )
                        )

                    else -> uiState.notifications.add(errorMapper.mapToNotification(it))
                }
            }
        )
    }
}
