package me.bookk.feature.authorization.data.di

import me.bookk.core.android.AndroidActivityAware
import me.bookk.feature.authorization.data.local.AndroidPassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module

internal actual fun authDataPlatformModule(): Module = module {
    single { AndroidPassKeyManager(get(named("relyingParty")), get()) } binds arrayOf(AndroidActivityAware::class, PassKeyManager::class)
}
