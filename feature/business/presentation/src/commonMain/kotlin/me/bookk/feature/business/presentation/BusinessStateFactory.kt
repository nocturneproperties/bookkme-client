package me.bookk.feature.business.presentation

import me.bookk.feature.business.presentation.bootstrap.BootstrapState
import me.bookk.feature.business.presentation.create.state.CreateBusinessState

interface BusinessStateFactory {
    fun createBusinessState(initData: CreateBusinessState.InitData): CreateBusinessState
    fun createBootstrapState(initData: BootstrapState.InitData): BootstrapState
}