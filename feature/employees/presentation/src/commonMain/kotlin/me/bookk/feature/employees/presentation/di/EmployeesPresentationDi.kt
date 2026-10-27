package me.bookk.feature.employees.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformEmployeesDiModule(): Module

fun employeesPresentationModule() = module {
    includes(platformEmployeesDiModule())
}
