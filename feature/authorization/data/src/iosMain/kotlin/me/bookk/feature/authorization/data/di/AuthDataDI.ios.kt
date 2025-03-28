package me.bookk.feature.authorization.data.di

import me.bookk.feature.authorization.data.local.IosPassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun authDataPlatformModule(): Module = module {
    factoryOf(::IosPassKeyManager) bind PassKeyManager::class
}