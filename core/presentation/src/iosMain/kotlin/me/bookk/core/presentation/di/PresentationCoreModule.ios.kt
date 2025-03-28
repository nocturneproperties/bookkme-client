package me.bookk.core.presentation.di

import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.IosDateLocalizer
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun presentationNativeModule(): Module = module {
    single<DateLocalizer> { IosDateLocalizer() }
}