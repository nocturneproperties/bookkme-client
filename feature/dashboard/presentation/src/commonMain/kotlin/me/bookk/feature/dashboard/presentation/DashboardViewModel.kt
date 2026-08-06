package me.bookk.feature.dashboard.presentation

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retry
import me.bookk.android.feature.dashboard.resources.DashboardRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessIdChanges
import me.bookk.feature.business.domain.api.plugin.ObserveAppointmentsPluginEnabled
import me.bookk.feature.dashboard.presentation.state.DashboardState
import me.bookk.feature.dashboard.presentation.state.HomeContent
import me.bookk.feature.dashboard.presentation.state.TabItem
import me.bookk.feature.dashboard.presentation.state.TabItemsState
import kotlin.uuid.Uuid

class DashboardViewModel(
    private val observeDashboardBusinessIdChanges: ObserveDashboardBusinessIdChanges,
    private val observeAppointmentsPluginEnabled: ObserveAppointmentsPluginEnabled,
    stateFactory: DashboardStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: DashboardState = stateFactory.createDashboardState(createInitData()).setup()

    private var businessId: Uuid? = null

    init {
        observeBusiness()
    }

    private fun DashboardState.setup() = apply {
        home.onboarding.onCreateBusinessClick = weakVMClosure {
            it.uiState.navigation.push(DashboardHomeNavigationDestination.CreateBusiness)
        }
        home.onboarding.onEnablePluginsClick = weakVMClosure { it.onEnablePluginsClick() }
    }

    private fun onEnablePluginsClick() {
        businessId?.let {
            uiState.navigation.push(DashboardHomeNavigationDestination.EnablePlugins(it))
        }
    }

    private fun observeBusiness() {
        observeDashboardBusinessIdChanges()
            .flatMapLatest { id ->
                if (id != null) {
                    observeAppointmentsPluginEnabled(id)
                        .map { enabled -> id to (if (enabled) HomeContent.ActivePlugin else HomeContent.Onboarding) }
                        .onStart { emit(id to HomeContent.Loading) }
                } else {
                    flowOf(id to HomeContent.Onboarding)
                }
            }
            .flowOn(DispatcherProvider.io)
            .onEach { (id, content) ->
                businessId = id
                uiState.tabItems.items.first { it.id == TabItem.Id.BUSINESS }.isEnabled = id != null
                uiState.home.onboarding.isBusinessStepDone = id != null
                uiState.home.onboarding.isPluginsStepUnlocked = id != null
                uiState.home.content = content
            }
            .retry()
            .launchIn(viewModelScope)
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
