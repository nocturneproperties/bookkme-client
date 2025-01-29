package me.bookk.feature.authorization.presentation.bootstrap

import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState

internal class AndroidBootstrapState : BootstrapState {
    override val navigation: NavigationState<BootstrapNavigationDestination> = AndroidNavigationState()
}