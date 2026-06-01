package me.bookk.di.feature

import me.bookk.feature.services.data.di.servicesDataModule
import me.bookk.feature.services.domain.impl.di.servicesDomainModule
import me.bookk.feature.services.presentation.di.servicesPresentationModule
import org.koin.dsl.module

internal fun servicesDiModule() = module {
    includes(
        servicesPresentationModule(),
        servicesDataModule(),
        servicesDomainModule()
    )
}