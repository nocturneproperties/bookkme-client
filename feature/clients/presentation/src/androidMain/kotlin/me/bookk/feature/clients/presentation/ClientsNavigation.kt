package me.bookk.feature.clients.presentation

import androidx.compose.runtime.compositionLocalOf
import kotlin.uuid.Uuid

class ClientsNavigation(
    val toAddClient: (Uuid) -> Unit,
    val toClientDetails: (Uuid) -> Unit,
    val toEditClient: (Uuid) -> Unit,
    val onBack: () -> Unit
)

internal val LocalNavigation = compositionLocalOf {
    ClientsNavigation(
        onBack = {},
        toAddClient = {},
        toClientDetails = {},
        toEditClient = {}
    )
}