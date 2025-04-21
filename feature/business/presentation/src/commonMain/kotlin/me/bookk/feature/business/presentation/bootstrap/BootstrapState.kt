package me.bookk.feature.business.presentation.bootstrap

import me.bookk.feature.business.presentation.navigation.BusinessDestination

interface BootstrapState {
    var startDestination: BusinessDestination

    class InitData(
        val initialDestination: BusinessDestination
    )
}