package me.bookk.feature.business.presentation.bootstrap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.feature.business.presentation.navigation.BusinessDestination

internal class AndroidBootstrapState(initData: BootstrapState.InitData) : BootstrapState {
    override var startDestination: BusinessDestination by mutableStateOf(initData.initialDestination)
}