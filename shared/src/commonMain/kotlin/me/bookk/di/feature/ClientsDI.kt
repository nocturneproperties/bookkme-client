package me.bookk.di.feature

import me.bookk.feature.clients.data.di.clientsDataModule
import me.bookk.feature.clients.domain.impl.di.clientsDomainModule
import me.bookk.feature.clients.presentation.di.clientsPresentationModule
import org.koin.dsl.module

internal fun clientsDiModule() = module {
    includes(
        clientsPresentationModule(),
        clientsDataModule(),
        clientsDomainModule()
    )
}