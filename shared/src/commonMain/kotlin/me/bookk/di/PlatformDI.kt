package me.bookk.di

import me.bookk.feature.platform.data.di.platformDataModule
import me.bookk.feature.platform.domain.impl.di.platformDomainModule
import org.koin.dsl.module

fun platformDiModule() = module {
    includes(
        platformDataModule(),
        platformDomainModule()
    )
}