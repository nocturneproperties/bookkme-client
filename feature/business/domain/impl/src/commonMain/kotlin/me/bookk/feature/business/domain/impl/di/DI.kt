package me.bookk.feature.business.domain.impl.di

import me.bookk.feature.business.domain.api.CreateBusiness
import me.bookk.feature.business.domain.api.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.GetBusinessById
import me.bookk.feature.business.domain.api.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.UpdateBusiness
import me.bookk.feature.business.domain.impl.CreateBusinessImpl
import me.bookk.feature.business.domain.impl.GetAvailableDashboardFeaturesImpl
import me.bookk.feature.business.domain.impl.GetBusinessByIdImpl
import me.bookk.feature.business.domain.impl.ObserveDashboardBusinessChangesImpl
import me.bookk.feature.business.domain.impl.RefreshBusinessInfoImpl
import me.bookk.feature.business.domain.impl.UpdateBusinessImpl
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
}