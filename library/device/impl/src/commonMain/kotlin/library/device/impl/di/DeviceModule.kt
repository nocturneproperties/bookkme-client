package library.device.impl.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformDeviceModule(): Module

fun deviceModule() = module {
    includes(platformDeviceModule())
}