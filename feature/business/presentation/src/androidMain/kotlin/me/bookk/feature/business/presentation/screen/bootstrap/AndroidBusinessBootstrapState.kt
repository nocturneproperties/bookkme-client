package me.bookk.feature.business.presentation.screen.bootstrap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.feature.business.presentation.navigation.BusinessDestination

internal class AndroidBusinessBootstrapState(initData: BusinessBootstrapState.InitData) :
    BusinessBootstrapState {
    override var startDestination: BusinessDestination by mutableStateOf(initData.initialDestination)
    override val notification: PresentationNotificationState = AndroidNotificationState()
}