package me.bookk.client.di.feature

import me.bookk.feature.services.data.di.servicesDataModule
import me.bookk.feature.services.domain.impl.di.clientServicesDomainModule
import org.koin.dsl.module

internal fun servicesDiModule() = module {
    includes(
        clientServicesDomainModule(),
        servicesDataModule(),
    )
}
