package library.biometry.impl.di

import library.biometry.api.Biometry
import library.biometry.impl.IosBiometry
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

internal actual fun biometryPlatformModule(): Module = module {
    singleOf(::IosBiometry) binds arrayOf(Biometry::class)
}