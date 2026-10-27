package library.device.impl.di

import library.device.api.DeviceFacade
import library.device.impl.IosDeviceFacade
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformDeviceModule(): Module = module {
    factoryOf(::IosDeviceFacade) bind DeviceFacade::class
}