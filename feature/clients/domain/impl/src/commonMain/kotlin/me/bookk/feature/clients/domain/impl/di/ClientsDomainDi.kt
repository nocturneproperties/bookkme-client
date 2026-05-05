package me.bookk.feature.clients.domain.impl.di

import me.bookk.feature.clients.domain.api.CreateClient
import me.bookk.feature.clients.domain.api.DeleteClient
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.impl.CreateClientImpl
import me.bookk.feature.clients.domain.impl.DeleteClientImpl
import me.bookk.feature.clients.domain.impl.GetClientsListImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun clientsDomainModule() = module {
    factoryOf(::GetClientsListImpl) bind GetClientsList::class
    factoryOf(::CreateClientImpl) bind CreateClient::class
    factoryOf(::DeleteClientImpl) bind DeleteClient::class
}