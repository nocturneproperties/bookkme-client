package me.bookk.feature.settings.domain.impl.di

import me.bookk.core.domain.fetch.AppStartAction
import me.bookk.core.domain.fetch.BackgroundDataFetchAction
import me.bookk.feature.settings.domain.api.AcceptLegalDocuments
import me.bookk.feature.settings.domain.api.CompleteTelegramConfirmation
import me.bookk.feature.settings.domain.api.CreateNewPasskey
import me.bookk.feature.settings.domain.api.DeleteAccount
import me.bookk.feature.settings.domain.api.DeleteExpiredPersonalDataExportFile
import me.bookk.feature.settings.domain.api.DeletePasskey
import me.bookk.feature.settings.domain.api.DownloadPersonalDataExport
import me.bookk.feature.settings.domain.api.EditProfile
import me.bookk.feature.settings.domain.api.GetAvailablePasskeys
import me.bookk.feature.settings.domain.api.GetColorScheme
import me.bookk.feature.settings.domain.api.GetCrashReportingEnabled
import me.bookk.feature.settings.domain.api.GetLegalDocumentStatuses
import me.bookk.feature.settings.domain.api.GetLegalDocumentVersions
import me.bookk.feature.settings.domain.api.GetNotificationSettings
import me.bookk.feature.settings.domain.api.GetPersonalDataExport
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.StartPersonalDataExport
import me.bookk.feature.settings.domain.api.UpdateColorScheme
import me.bookk.feature.settings.domain.api.UpdateCrashReportingEnabled
import me.bookk.feature.settings.domain.api.UpdateNotificationSettings
import me.bookk.feature.settings.domain.api.UpdateNotificationToken
import me.bookk.feature.settings.domain.impl.AcceptLegalDocumentsImpl
import me.bookk.feature.settings.domain.impl.CompleteTelegramConfirmationImpl
import me.bookk.feature.settings.domain.impl.CreateNewPasskeyImpl
import me.bookk.feature.settings.domain.impl.DeleteAccountImpl
import me.bookk.feature.settings.domain.impl.DeleteExpiredPersonalDataExportFileImpl
import me.bookk.feature.settings.domain.impl.DeletePasskeyImpl
import me.bookk.feature.settings.domain.impl.DownloadPersonalDataExportImpl
import me.bookk.feature.settings.domain.impl.EditProfileImpl
import me.bookk.feature.settings.domain.impl.GetAvailablePasskeysImpl
import me.bookk.feature.settings.domain.impl.GetColorSchemeImpl
import me.bookk.feature.settings.domain.impl.GetCrashReportingEnabledImpl
import me.bookk.feature.settings.domain.impl.GetLegalDocumentStatusesImpl
import me.bookk.feature.settings.domain.impl.GetLegalDocumentVersionsImpl
import me.bookk.feature.settings.domain.impl.GetNotificationSettingsImpl
import me.bookk.feature.settings.domain.impl.GetPersonalDataExportImpl
import me.bookk.feature.settings.domain.impl.GetSettingsImpl
import me.bookk.feature.settings.domain.impl.StartPersonalDataExportImpl
import me.bookk.feature.settings.domain.impl.TelegramDeleteAccountImpl
import me.bookk.feature.settings.domain.impl.TelegramStartPersonalDataExportImpl
import me.bookk.feature.settings.domain.impl.UpdateColorSchemeImpl
import me.bookk.feature.settings.domain.impl.UpdateCrashReportingEnabledImpl
import me.bookk.feature.settings.domain.impl.UpdateNotificationSettingsImpl
import me.bookk.feature.settings.domain.impl.UpdateNotificationTokenImpl
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

fun settingsDomainModule(): Module {
    return module {
        includes(sharedSettingsDomainModule())
        factoryOf(::DeleteAccountImpl) bind DeleteAccount::class
        factoryOf(::StartPersonalDataExportImpl) bind StartPersonalDataExport::class
    }
}

fun clientSettingsDomainModule(): Module {
    return module {
        includes(sharedSettingsDomainModule())
        factoryOf(::TelegramDeleteAccountImpl) bind DeleteAccount::class
        factoryOf(::TelegramStartPersonalDataExportImpl) bind StartPersonalDataExport::class
        factoryOf(::CompleteTelegramConfirmationImpl) bind CompleteTelegramConfirmation::class
    }
}

private fun sharedSettingsDomainModule() = module {
    factoryOf(::DeleteExpiredPersonalDataExportFileImpl) binds
        arrayOf(DeleteExpiredPersonalDataExportFile::class, AppStartAction::class)
    factoryOf(::DownloadPersonalDataExportImpl) bind DownloadPersonalDataExport::class
    factoryOf(::GetPersonalDataExportImpl) bind GetPersonalDataExport::class
    factoryOf(::AcceptLegalDocumentsImpl) bind AcceptLegalDocuments::class
    factoryOf(::GetLegalDocumentStatusesImpl) bind GetLegalDocumentStatuses::class
    factoryOf(::GetLegalDocumentVersionsImpl) bind GetLegalDocumentVersions::class
    factoryOf(::GetColorSchemeImpl) bind GetColorScheme::class
    factoryOf(::UpdateColorSchemeImpl) bind UpdateColorScheme::class
    factoryOf(::GetCrashReportingEnabledImpl) bind GetCrashReportingEnabled::class
    factoryOf(::UpdateCrashReportingEnabledImpl) bind UpdateCrashReportingEnabled::class
    factoryOf(::GetSettingsImpl) bind GetSettings::class
    factoryOf(::EditProfileImpl) bind EditProfile::class
    factoryOf(::GetAvailablePasskeysImpl) bind GetAvailablePasskeys::class
    factoryOf(::DeletePasskeyImpl) bind DeletePasskey::class
    factoryOf(::CreateNewPasskeyImpl) bind CreateNewPasskey::class
    factoryOf(::GetNotificationSettingsImpl) binds
        arrayOf(GetNotificationSettings::class, BackgroundDataFetchAction::class)
    factoryOf(::UpdateNotificationSettingsImpl) bind UpdateNotificationSettings::class
    factoryOf(::UpdateNotificationTokenImpl) binds
        arrayOf(UpdateNotificationToken::class, BackgroundDataFetchAction::class)
}
