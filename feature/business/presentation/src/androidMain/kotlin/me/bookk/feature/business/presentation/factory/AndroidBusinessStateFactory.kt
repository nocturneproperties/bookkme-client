package me.bookk.feature.business.presentation.factory

import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.bootstrap.AndroidBusinessBootstrapState
import me.bookk.feature.business.presentation.create.AndroidCreateBusinessState
import me.bookk.feature.business.presentation.dashboard.AndroidDashboardState
import me.bookk.feature.business.presentation.screen.bootstrap.BusinessBootstrapState
import me.bookk.feature.business.presentation.screen.create.state.CreateBusinessState
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardState
import me.bookk.feature.business.presentation.screen.settings.state.BusinessSettingsState
import me.bookk.feature.business.presentation.settings.AndroidBusinessSettingsState

class AndroidBusinessStateFactory : BusinessStateFactory {
    override fun createBusinessState(initData: CreateBusinessState.InitData): CreateBusinessState {
        return AndroidCreateBusinessState(initData)
    }

    override fun createBootstrapState(initData: BusinessBootstrapState.InitData): BusinessBootstrapState {
        return AndroidBusinessBootstrapState(initData)
    }

    override fun createBusinessDashboardState(initData: BusinessDashboardState.InitData): BusinessDashboardState {
        return AndroidDashboardState(initData)
    }

    override fun createBusinessSettingsState(initData: BusinessSettingsState.InitData): BusinessSettingsState {
        return AndroidBusinessSettingsState(initData)
    }

}