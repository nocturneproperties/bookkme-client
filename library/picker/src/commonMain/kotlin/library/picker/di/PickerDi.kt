package library.picker.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformPickerDiModule(): Module

fun pickerModule() = module {
    includes(platformPickerDiModule())
}