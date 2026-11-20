package me.bookk.feature.authorization.presentation.screen.clientbootstrap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapNavigationDestination

internal class WebClientBootstrapState : ClientBootstrapState {
    override var startDestination: BootstrapNavigationDestination? by mutableStateOf(null)
}
