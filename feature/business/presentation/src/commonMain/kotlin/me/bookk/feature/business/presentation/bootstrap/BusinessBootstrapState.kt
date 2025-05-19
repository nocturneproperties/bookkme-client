package me.bookk.feature.business.presentation.bootstrap

import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.feature.business.presentation.navigation.BusinessDestination

interface BusinessBootstrapState {
    var startDestination: BusinessDestination
    val notification: PresentationNotificationState

    class InitData(
        val initialDestination: BusinessDestination
    )
}