package me.bookk.feature.authorization.presentation.bootstrap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState.UIColorScheme

internal class AndroidBootstrapState : BootstrapState {
    override var colorScheme: UIColorScheme by mutableStateOf(UIColorScheme.SYSTEM)
    override val navigation: NavigationState<BootstrapNavigationDestination> = AndroidNavigationState()
}