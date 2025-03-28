package me.bookk.core.presentation.di

import me.bookk.core.presentation.VmArgs
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun presentationNativeModule(): Module

fun presentationCoreModule() = module {
    single { VmArgs(get()) }
    includes(presentationNativeModule())
}