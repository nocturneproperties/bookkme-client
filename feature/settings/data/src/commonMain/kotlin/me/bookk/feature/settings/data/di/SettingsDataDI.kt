package me.bookk.feature.settings.data.di

import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.settings.data.datasource.CommonDataExportDataSource
import me.bookk.feature.settings.data.datasource.CommonLegalDocumentsDataSource
import me.bookk.feature.settings.data.datasource.CommonNotificationSettingsDataSource
import me.bookk.feature.settings.data.datasource.CommonSettingsDataSource
import me.bookk.feature.settings.data.datasource.PasskeySettingsDataSourceImpl
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import me.bookk.feature.settings.domain.datasource.LegalDocumentsDataSource
import me.bookk.feature.settings.domain.datasource.NotificationSettingsDataSource
import me.bookk.feature.settings.domain.datasource.SettingsDataSource
import me.bookk.feature.settings.domain.datasource.passkey.PasskeySettingsDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

fun settingsDataModule() = module {
    singleOf(::CommonDataExportDataSource) binds
        arrayOf(DataExportDataSource::class, LogOutAction::class)
    singleOf(::CommonLegalDocumentsDataSource) binds
        arrayOf(LegalDocumentsDataSource::class, LogOutAction::class)
    singleOf(::CommonSettingsDataSource) bind SettingsDataSource::class
    singleOf(::PasskeySettingsDataSourceImpl) bind PasskeySettingsDataSource::class
    singleOf(::CommonNotificationSettingsDataSource) binds
        arrayOf(NotificationSettingsDataSource::class, LogOutAction::class)
}
