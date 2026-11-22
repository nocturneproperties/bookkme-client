package me.bookk.feature.business.domain.impl.di

import me.bookk.core.domain.fetch.AppStartAction
import me.bookk.core.domain.fetch.BackgroundDataFetchAction
import me.bookk.core.domain.fetch.DataFetchAction
import me.bookk.feature.business.domain.api.DeleteExpiredBusinessDataExportFiles
import me.bookk.feature.business.domain.api.DownloadBusinessDataExport
import me.bookk.feature.business.domain.api.GetBusinessDataExport
import me.bookk.feature.business.domain.api.StartBusinessDataExport
import me.bookk.feature.business.domain.api.business.CanEditBusiness
import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.domain.api.business.GetAvailableDashboardFeatures
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.business.domain.api.business.JoinBusiness
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessIdChanges
import me.bookk.feature.business.domain.api.business.ObserveDashboardSetupStatus
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.business.RefreshDashboardSetupStatus
import me.bookk.feature.business.domain.api.business.SwitchDashboardBusiness
import me.bookk.feature.business.domain.api.business.UpdateBusiness
import me.bookk.feature.business.domain.api.link.GetBusinessLink
import me.bookk.feature.business.domain.api.link.ResolveBusinessLink
import me.bookk.feature.business.domain.api.link.SetBusinessLink
import me.bookk.feature.business.domain.api.plugin.EnableAppointmentsPlugin
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.domain.impl.DeleteExpiredBusinessDataExportFilesImpl
import me.bookk.feature.business.domain.impl.DownloadBusinessDataExportImpl
import me.bookk.feature.business.domain.impl.GetBusinessDataExportImpl
import me.bookk.feature.business.domain.impl.StartBusinessDataExportImpl
import me.bookk.feature.business.domain.impl.business.CanEditBusinessImpl
import me.bookk.feature.business.domain.impl.business.CreateBusinessImpl
import me.bookk.feature.business.domain.impl.business.DashboardBusinessDataFetch
import me.bookk.feature.business.domain.impl.business.GetAvailableDashboardFeaturesImpl
import me.bookk.feature.business.domain.impl.business.GetBusinessesImpl
import me.bookk.feature.business.domain.impl.business.JoinBusinessImpl
import me.bookk.feature.business.domain.impl.business.ObserveDashboardBusinessChangesImpl
import me.bookk.feature.business.domain.impl.business.ObserveDashboardBusinessIdChangesImpl
import me.bookk.feature.business.domain.impl.business.ObserveDashboardSetupStatusImpl
import me.bookk.feature.business.domain.impl.business.ObserveUserBusinessesChangesImpl
import me.bookk.feature.business.domain.impl.business.RefreshBusinessInfoImpl
import me.bookk.feature.business.domain.impl.business.RefreshDashboardSetupStatusImpl
import me.bookk.feature.business.domain.impl.business.SwitchDashboardBusinessImpl
import me.bookk.feature.business.domain.impl.business.UpdateBusinessImpl
import me.bookk.feature.business.domain.impl.link.GetBusinessLinkImpl
import me.bookk.feature.business.domain.impl.link.ResolveBusinessLinkImpl
import me.bookk.feature.business.domain.impl.link.SetBusinessLinkImpl
import me.bookk.feature.business.domain.impl.plugin.EnableAppointmentsPluginImpl
import me.bookk.feature.business.domain.impl.plugin.IsAppointmentsPluginEnabledImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

fun businessDomainModule() = module {
    factoryOf(::DeleteExpiredBusinessDataExportFilesImpl) binds
        arrayOf(DeleteExpiredBusinessDataExportFiles::class, AppStartAction::class)
    factoryOf(::DownloadBusinessDataExportImpl) bind DownloadBusinessDataExport::class
    factoryOf(::GetBusinessDataExportImpl) bind GetBusinessDataExport::class
    factoryOf(::StartBusinessDataExportImpl) bind StartBusinessDataExport::class
    factoryOf(::CreateBusinessImpl) bind CreateBusiness::class
    factoryOf(::RefreshBusinessInfoImpl) binds
        arrayOf(RefreshBusinessInfo::class, DataFetchAction::class)
    factory { DashboardBusinessDataFetch(get(), getAll()) } bind BackgroundDataFetchAction::class
    factoryOf(::ObserveDashboardBusinessChangesImpl) bind ObserveDashboardBusinessChanges::class
    factoryOf(::ObserveDashboardBusinessIdChangesImpl) bind ObserveDashboardBusinessIdChanges::class
    factoryOf(::ObserveUserBusinessesChangesImpl) bind ObserveUserBusinessesChanges::class
    factoryOf(::SwitchDashboardBusinessImpl) bind SwitchDashboardBusiness::class
    factoryOf(::JoinBusinessImpl) bind JoinBusiness::class
    factoryOf(::GetAvailableDashboardFeaturesImpl) bind GetAvailableDashboardFeatures::class
    factoryOf(::ObserveDashboardSetupStatusImpl) bind ObserveDashboardSetupStatus::class
    factoryOf(::RefreshDashboardSetupStatusImpl) bind RefreshDashboardSetupStatus::class
    factoryOf(::UpdateBusinessImpl) bind UpdateBusiness::class
    factoryOf(::EnableAppointmentsPluginImpl) bind EnableAppointmentsPlugin::class
    factoryOf(::IsAppointmentsPluginEnabledImpl) bind IsAppointmentsPluginEnabled::class
    factoryOf(::CanEditBusinessImpl) bind CanEditBusiness::class
    factoryOf(::GetBusinessLinkImpl) bind GetBusinessLink::class
    factoryOf(::SetBusinessLinkImpl) bind SetBusinessLink::class
}

fun clientBusinessDomainModule() = module {
    factoryOf(::GetBusinessesImpl) bind GetBusinesses::class
    factoryOf(::ResolveBusinessLinkImpl) bind ResolveBusinessLink::class
}
