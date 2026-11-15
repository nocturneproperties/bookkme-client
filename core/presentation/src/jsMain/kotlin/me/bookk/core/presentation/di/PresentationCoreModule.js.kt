package me.bookk.core.presentation.di

import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.JsDateLocalizer
import me.bookk.core.presentation.text.JsTextCollator
import me.bookk.core.presentation.text.TextCollator
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun presentationNativeModule(): Module {
    return module {
        single<DateLocalizer> { JsDateLocalizer() }
        single<TextCollator> { JsTextCollator() }
    }
}
