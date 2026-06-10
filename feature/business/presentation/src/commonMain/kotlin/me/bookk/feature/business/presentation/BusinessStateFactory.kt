package me.bookk.feature.business.presentation

import me.bookk.feature.business.presentation.screen.bootstrap.BusinessBootstrapState
import me.bookk.feature.business.presentation.screen.create.state.CreateBusinessState
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardState
import me.bookk.feature.business.presentation.screen.plugins.BusinessPluginListState
import me.bookk.feature.business.presentation.screen.plugins.BusinessPluginState
import me.bookk.feature.business.presentation.screen.settings.state.BusinessSettingsState

interface BusinessStateFactory {
    fun createBootstrapState(initData: BusinessBootstrapState.InitData): BusinessBootstrapState
    fun createBusinessState(initData: CreateBusinessState.InitData): CreateBusinessState
    fun createBusinessDashboardState(): BusinessDashboardState
    fun createBusinessSettingsState(initData: BusinessSettingsState.InitData): BusinessSettingsState
    fun createBusinessPluginListState(): BusinessPluginListState
    fun createBusinessPluginState(): BusinessPluginState
}