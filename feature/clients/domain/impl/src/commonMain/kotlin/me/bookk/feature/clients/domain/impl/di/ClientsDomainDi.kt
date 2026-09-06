package me.bookk.feature.clients.domain.impl.di

import me.bookk.feature.clients.domain.api.CreateClient
import me.bookk.feature.clients.domain.api.DeleteClient
import me.bookk.feature.clients.domain.api.EditClient
import me.bookk.feature.clients.domain.api.GetClient
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.clients.domain.impl.CreateClientImpl
import me.bookk.feature.clients.domain.impl.DeleteClientImpl
import me.bookk.feature.clients.domain.impl.EditClientImpl
import me.bookk.feature.clients.domain.impl.GetClientImpl
import me.bookk.feature.clients.domain.impl.GetClientsListImpl
import me.bookk.feature.clients.domain.impl.ObserveCurrentBusinessIdImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun clientsDomainModule() = module {
    factoryOf(::GetClientsListImpl) bind GetClientsList::class
    factoryOf(::CreateClientImpl) bind CreateClient::class
    factoryOf(::EditClientImpl) bind EditClient::class
    factoryOf(::DeleteClientImpl) bind DeleteClient::class
    factoryOf(::GetClientImpl) bind GetClient::class
    factoryOf(::ObserveCurrentBusinessIdImpl) bind ObserveCurrentBusinessId::class
}