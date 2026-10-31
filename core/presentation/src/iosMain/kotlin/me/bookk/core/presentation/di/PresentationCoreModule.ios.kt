package me.bookk.core.presentation.di

import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.IosDateLocalizer
import me.bookk.core.presentation.text.IosTextCollator
import me.bookk.core.presentation.text.TextCollator
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun presentationNativeModule(): Module = module {
    single<DateLocalizer> { IosDateLocalizer() }
    single<TextCollator> { IosTextCollator() }
}