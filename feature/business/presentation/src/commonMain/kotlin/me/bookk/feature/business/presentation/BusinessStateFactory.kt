package me.bookk.feature.business.presentation

import me.bookk.feature.business.presentation.bootstrap.BusinessBootstrapState
import me.bookk.feature.business.presentation.create.state.CreateBusinessState

interface BusinessStateFactory {
    fun createBusinessState(initData: CreateBusinessState.InitData): CreateBusinessState
    fun createBootstrapState(initData: BusinessBootstrapState.InitData): BusinessBootstrapState
}