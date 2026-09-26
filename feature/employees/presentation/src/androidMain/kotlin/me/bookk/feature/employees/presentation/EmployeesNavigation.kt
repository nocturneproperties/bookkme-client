package me.bookk.feature.employees.presentation

import androidx.compose.runtime.compositionLocalOf
import kotlin.uuid.Uuid

class EmployeesNavigation(
    val toInviteEmployee: (Uuid) -> Unit,
    val toEditEmployee: (Uuid) -> Unit,
    val onBack: () -> Unit
)

internal val LocalNavigation = compositionLocalOf {
    EmployeesNavigation(
        toInviteEmployee = {},
        toEditEmployee = {},
        onBack = {}
    )
}
