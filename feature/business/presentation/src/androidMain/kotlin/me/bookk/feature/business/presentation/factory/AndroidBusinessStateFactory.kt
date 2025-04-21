package me.bookk.feature.business.presentation.factory

import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.bootstrap.AndroidBootstrapState
import me.bookk.feature.business.presentation.bootstrap.BootstrapState
import me.bookk.feature.business.presentation.create.AndroidCreateBusinessState
import me.bookk.feature.business.presentation.create.state.CreateBusinessState

class AndroidBusinessStateFactory : BusinessStateFactory {
    override fun createBusinessState(initData: CreateBusinessState.InitData): CreateBusinessState {
        return AndroidCreateBusinessState(initData)
    }

    override fun createBootstrapState(initData: BootstrapState.InitData): BootstrapState {
        return AndroidBootstrapState(initData)
    }

}