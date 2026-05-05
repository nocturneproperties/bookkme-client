package me.bookk.feature.clients.presentation

import androidx.compose.runtime.compositionLocalOf

class ClientsNavigation(
    val onBack: () -> Unit
)

internal val LocalNavigation = compositionLocalOf {
    ClientsNavigation(
        onBack = {}
    )
}