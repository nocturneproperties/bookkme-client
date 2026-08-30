package me.bookk.feature.services.domain.impl.di

import me.bookk.feature.services.domain.api.GetBusinessCurrency
import me.bookk.feature.services.domain.api.group.CreateServiceGroup
import me.bookk.feature.services.domain.api.group.DeleteServiceGroup
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.quote.CreateQuote
import me.bookk.feature.services.domain.api.service.CreateService
import me.bookk.feature.services.domain.api.service.DeleteService
import me.bookk.feature.services.domain.api.service.EditService
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.domain.impl.GetBusinessCurrencyImpl
import me.bookk.feature.services.domain.impl.group.CreateServiceGroupImpl
import me.bookk.feature.services.domain.impl.group.DeleteServiceGroupImpl
import me.bookk.feature.services.domain.impl.group.GetServiceGroupsImpl
import me.bookk.feature.services.domain.impl.quote.CreateQuoteImpl
import me.bookk.feature.services.domain.impl.service.CreateServiceImpl
import me.bookk.feature.services.domain.impl.service.DeleteServiceImpl
import me.bookk.feature.services.domain.impl.service.EditServiceImpl
import me.bookk.feature.services.domain.impl.service.GetServicesImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun servicesDomainModule() = module {
    factoryOf(::CreateServiceImpl) bind CreateService::class
    factoryOf(::DeleteServiceImpl) bind DeleteService::class
    factoryOf(::EditServiceImpl) bind EditService::class
    factoryOf(::GetServicesImpl) bind GetServices::class

    factoryOf(::CreateServiceGroupImpl) bind CreateServiceGroup::class
    factoryOf(::DeleteServiceGroupImpl) bind DeleteServiceGroup::class
    factoryOf(::GetServiceGroupsImpl) bind GetServiceGroups::class
    factoryOf(::GetBusinessCurrencyImpl) bind GetBusinessCurrency::class

    factoryOf(::CreateQuoteImpl) bind CreateQuote::class
}