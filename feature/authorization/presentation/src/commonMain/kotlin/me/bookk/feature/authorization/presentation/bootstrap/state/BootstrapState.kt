package me.bookk.feature.authorization.presentation.bootstrap.state

import me.bookk.designsystem.uistate.NavigationState
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapNavigationDestination

interface BootstrapState {
    val navigation: NavigationState<BootstrapNavigationDestination>
}