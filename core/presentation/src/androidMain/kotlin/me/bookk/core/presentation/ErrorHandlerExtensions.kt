package me.bookk.core.presentation

import androidx.compose.runtime.staticCompositionLocalOf
import me.bookk.core.presentation.error.ErrorHandler
import me.bookk.core.presentation.error.PresentationError

val LocalErrorHandler = staticCompositionLocalOf<ErrorHandler> {
    object : ErrorHandler {
        override fun handle(e: PresentationError) {
            //NOOP
        }
    }
}