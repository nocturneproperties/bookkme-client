package me.bookk.di.feature

import me.bookk.feature.platform.data.di.platformDataModule
import me.bookk.feature.platform.domain.impl.di.platformDomainModule
import org.koin.dsl.module

internal fun platformDiModule() = module {
    includes(
        platformDataModule(),
        platformDomainModule()
    )
}