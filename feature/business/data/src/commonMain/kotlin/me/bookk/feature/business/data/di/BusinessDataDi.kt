package me.bookk.feature.business.data.di

import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.business.data.datasource.CommonBusinessDataSource
import me.bookk.feature.business.data.datasource.CommonBusinessLinkDataSource
import me.bookk.feature.business.data.datasource.CommonDataExportDataSource
import me.bookk.feature.business.data.datasource.CommonPluginDataSource
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.datasource.BusinessLinkDataSource
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import me.bookk.feature.business.domain.datasource.PluginDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.binds
import org.koin.dsl.module

fun businessDataModule() = module {
    singleOf(::CommonDataExportDataSource) binds
        arrayOf(DataExportDataSource::class, LogOutAction::class)
    singleOf(::CommonBusinessDataSource) binds
        arrayOf(BusinessDataSource::class, LogOutAction::class)
    singleOf(::CommonBusinessLinkDataSource) binds
        arrayOf(BusinessLinkDataSource::class, LogOutAction::class)
    singleOf(::CommonPluginDataSource) binds arrayOf(PluginDataSource::class, LogOutAction::class)
}

fun clientBusinessDataModule() = module {
    singleOf(::CommonBusinessDataSource) binds
        arrayOf(BusinessDataSource::class, LogOutAction::class)
}
