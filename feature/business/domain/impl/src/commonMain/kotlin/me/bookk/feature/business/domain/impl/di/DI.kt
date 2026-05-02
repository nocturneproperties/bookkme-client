package me.bookk.feature.business.domain.impl.di

import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.domain.api.business.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.business.GetBusinessById
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.business.UpdateBusiness
import me.bookk.feature.business.domain.api.client.CreateClient
import me.bookk.feature.business.domain.api.client.DeleteClient
import me.bookk.feature.business.domain.api.client.GetClientsList
import me.bookk.feature.business.domain.impl.business.CreateBusinessImpl
import me.bookk.feature.business.domain.impl.business.GetAvailableDashboardFeaturesImpl
import me.bookk.feature.business.domain.impl.business.GetBusinessByIdImpl
import me.bookk.feature.business.domain.impl.business.ObserveDashboardBusinessChangesImpl
import me.bookk.feature.business.domain.impl.business.RefreshBusinessInfoImpl
import me.bookk.feature.business.domain.impl.business.UpdateBusinessImpl
import me.bookk.feature.business.domain.impl.client.CreateClientImpl
import me.bookk.feature.business.domain.impl.client.DeleteClientImpl
import me.bookk.feature.business.domain.impl.client.GetClientsListImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun businessDomainModule() = module {
    factoryOf(::CreateBusinessImpl) bind CreateBusiness::class
    factoryOf(::RefreshBusinessInfoImpl) bind RefreshBusinessInfo::class
    factoryOf(::ObserveDashboardBusinessChangesImpl) bind ObserveDashboardBusinessChanges::class
    factoryOf(::GetAvailableDashboardFeaturesImpl) bind GetAvailableDashboardFeatures::class
    factoryOf(::GetBusinessByIdImpl) bind GetBusinessById::class
    factoryOf(::UpdateBusinessImpl) bind UpdateBusiness::class
    factoryOf(::GetClientsListImpl) bind GetClientsList::class
    factoryOf(::CreateClientImpl) bind CreateClient::class
    factoryOf(::DeleteClientImpl) bind DeleteClient::class
}