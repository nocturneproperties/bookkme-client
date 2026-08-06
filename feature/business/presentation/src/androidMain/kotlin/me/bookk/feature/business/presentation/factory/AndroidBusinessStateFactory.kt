package me.bookk.feature.business.presentation.factory

import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.screen.create.AndroidCreateBusinessState
import me.bookk.feature.business.presentation.screen.create.state.CreateBusinessState
import me.bookk.feature.business.presentation.screen.dashboard.AndroidDashboardState
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardState
import me.bookk.feature.business.presentation.screen.plugins.AndroidBusinessPluginListState
import me.bookk.feature.business.presentation.screen.plugins.AndroidBusinessPluginState
import me.bookk.feature.business.presentation.screen.plugins.BusinessPluginListState
import me.bookk.feature.business.presentation.screen.plugins.BusinessPluginState
import me.bookk.feature.business.presentation.screen.settings.AndroidBusinessSettingsState
import me.bookk.feature.business.presentation.screen.settings.state.BusinessSettingsState

class AndroidBusinessStateFactory : BusinessStateFactory {
    override fun createBusinessState(initData: CreateBusinessState.InitData): CreateBusinessState {
        return AndroidCreateBusinessState(initData)
    }

    override fun createBusinessDashboardState(): BusinessDashboardState {
        return AndroidDashboardState()
    }

    override fun createBusinessSettingsState(initData: BusinessSettingsState.InitData): BusinessSettingsState {
        return AndroidBusinessSettingsState(initData)
    }

    override fun createBusinessPluginListState(): BusinessPluginListState {
        return AndroidBusinessPluginListState()
    }

    override fun createBusinessPluginState(): BusinessPluginState {
        return AndroidBusinessPluginState()
    }

}