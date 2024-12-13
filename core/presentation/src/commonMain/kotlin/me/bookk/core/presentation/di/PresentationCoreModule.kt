package me.bookk.core.presentation.di

import me.bookk.core.presentation.VmArgs
import org.koin.dsl.module

fun presentationCoreModule() = module {
    single { VmArgs(get(), get()) }
}