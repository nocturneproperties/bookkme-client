package library.biometry.impl.di

import library.biometry.api.BiometryOptManager
import library.biometry.impl.CommonBiometryOptManager
import me.bookk.core.domain.logout.LogOutAction
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

internal expect fun biometryPlatformModule(): Module

fun biometryModule() = module {
    includes(biometryPlatformModule())
    singleOf(::CommonBiometryOptManager) binds arrayOf(BiometryOptManager::class, LogOutAction::class)
}