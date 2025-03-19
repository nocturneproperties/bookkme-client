package me.bookk.core.presentation

import androidx.compose.runtime.staticCompositionLocalOf

fun interface UnauthorizedHandler {
    fun onUnauthorized()
}

val LocalUnauthorizedHandler = staticCompositionLocalOf { UnauthorizedHandler {} }