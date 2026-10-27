package library.biometry.impl.di

import library.biometry.api.Biometry
import library.biometry.impl.AndroidBiometry
import me.bookk.core.android.AndroidActivityAware
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

internal actual fun biometryPlatformModule(): Module = module {
    singleOf(::AndroidBiometry) binds arrayOf(AndroidActivityAware::class, Biometry::class)
}