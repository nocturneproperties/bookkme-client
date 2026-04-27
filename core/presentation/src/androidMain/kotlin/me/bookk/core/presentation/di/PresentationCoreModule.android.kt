package me.bookk.core.presentation.di

import me.bookk.core.presentation.date.AndroidDateLocalizer
import me.bookk.core.presentation.date.DateLocalizer
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun presentationNativeModule(): Module = module {
    single<DateLocalizer> { AndroidDateLocalizer() }
}