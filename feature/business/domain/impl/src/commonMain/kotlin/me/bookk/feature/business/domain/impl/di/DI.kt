package me.bookk.feature.business.domain.impl.di

import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.domain.api.business.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.business.GetBusinessById
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessIdChanges
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.business.UpdateBusiness
import me.bookk.feature.business.domain.api.plugin.EnableAppointmentsPlugin
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.domain.api.plugin.ObserveAppointmentsPluginEnabled
import me.bookk.feature.business.domain.impl.business.CreateBusinessImpl
import me.bookk.feature.business.domain.impl.business.GetAvailableDashboardFeaturesImpl
import me.bookk.feature.business.domain.impl.business.GetBusinessByIdImpl
import me.bookk.feature.business.domain.impl.business.ObserveDashboardBusinessChangesImpl
import me.bookk.feature.business.domain.impl.business.ObserveDashboardBusinessIdChangesImpl
import me.bookk.feature.business.domain.impl.business.RefreshBusinessInfoImpl
import me.bookk.feature.business.domain.impl.business.UpdateBusinessImpl
import me.bookk.feature.business.domain.impl.plugin.EnableAppointmentsPluginImpl
import me.bookk.feature.business.domain.impl.plugin.IsAppointmentsPluginEnabledImpl
import me.bookk.feature.business.domain.impl.plugin.ObserveAppointmentsPluginEnabledImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun businessDomainModule() = module {
    factoryOf(::CreateBusinessImpl) bind CreateBusiness::class
    factoryOf(::RefreshBusinessInfoImpl) bind RefreshBusinessInfo::class
    factoryOf(::ObserveDashboardBusinessChangesImpl) bind ObserveDashboardBusinessChanges::class
    factoryOf(::ObserveDashboardBusinessIdChangesImpl) bind ObserveDashboardBusinessIdChanges::class
    factoryOf(::GetAvailableDashboardFeaturesImpl) bind GetAvailableDashboardFeatures::class
    factoryOf(::GetBusinessByIdImpl) bind GetBusinessById::class
    factoryOf(::UpdateBusinessImpl) bind UpdateBusiness::class
    factoryOf(::EnableAppointmentsPluginImpl) bind EnableAppointmentsPlugin::class
    factoryOf(::IsAppointmentsPluginEnabledImpl) bind IsAppointmentsPluginEnabled::class
    factoryOf(::ObserveAppointmentsPluginEnabledImpl) bind ObserveAppointmentsPluginEnabled::class
}