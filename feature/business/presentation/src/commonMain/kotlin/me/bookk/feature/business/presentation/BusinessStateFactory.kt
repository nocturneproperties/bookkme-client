package me.bookk.feature.business.presentation

import me.bookk.feature.business.presentation.bootstrap.BusinessBootstrapState
import me.bookk.feature.business.presentation.create.state.CreateBusinessState
import me.bookk.feature.business.presentation.dashboard.state.BusinessDashboardState

interface BusinessStateFactory {
    fun createBootstrapState(initData: BusinessBootstrapState.InitData): BusinessBootstrapState
    fun createBusinessState(initData: CreateBusinessState.InitData): CreateBusinessState
    fun createBusinessDashboardState(initData: BusinessDashboardState.InitData): BusinessDashboardState

}