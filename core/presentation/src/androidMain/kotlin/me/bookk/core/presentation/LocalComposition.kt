package me.bookk.core.presentation

import androidx.compose.runtime.staticCompositionLocalOf

val LocalUnauthorizedHandler = staticCompositionLocalOf { UnauthorizedHandler {} }

fun interface BusinessAccessSuspendedHandler {
    fun onBusinessAccessSuspended()
}

val LocalBusinessAccessSuspendedHandler =
    staticCompositionLocalOf { BusinessAccessSuspendedHandler {} }
