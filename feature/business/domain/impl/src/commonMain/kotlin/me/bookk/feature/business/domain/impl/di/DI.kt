package me.bookk.feature.business.domain.impl.di

import me.bookk.feature.business.domain.api.CreateBusiness
import me.bookk.feature.business.domain.api.ObserveBusinessChanges
import me.bookk.feature.business.domain.api.RefreshBusinessInfo
import me.bookk.feature.business.domain.impl.CreateBusinessImpl
import me.bookk.feature.business.domain.impl.ObserveBusinessChangesImpl
import me.bookk.feature.business.domain.impl.RefreshBusinessInfoImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun businessDomainModule() = module {
    factoryOf(::CreateBusinessImpl) bind CreateBusiness::class
    factoryOf(::RefreshBusinessInfoImpl) bind RefreshBusinessInfo::class
    factoryOf(::ObserveBusinessChangesImpl) bind ObserveBusinessChanges::class
}