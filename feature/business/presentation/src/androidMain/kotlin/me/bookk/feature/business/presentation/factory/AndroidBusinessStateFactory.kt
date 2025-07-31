package me.bookk.feature.business.presentation.factory

import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.bootstrap.AndroidBusinessBootstrapState
import me.bookk.feature.business.presentation.bootstrap.BusinessBootstrapState
import me.bookk.feature.business.presentation.create.AndroidCreateBusinessState
import me.bookk.feature.business.presentation.create.state.CreateBusinessState
import me.bookk.feature.business.presentation.dashboard.AndroidDashboardState
import me.bookk.feature.business.presentation.dashboard.state.BusinessDashboardState
import me.bookk.feature.business.presentation.settings.AndroidBusinessSettingsState
import me.bookk.feature.business.presentation.settings.state.BusinessSettingsState

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