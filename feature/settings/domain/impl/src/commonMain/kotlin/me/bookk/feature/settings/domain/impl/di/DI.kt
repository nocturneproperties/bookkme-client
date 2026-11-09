package me.bookk.feature.settings.domain.impl.di

import me.bookk.feature.settings.domain.api.AcceptLegalDocuments
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
import me.bookk.feature.settings.domain.impl.UpdateColorSchemeImpl
import me.bookk.feature.settings.domain.impl.UpdateCrashReportingEnabledImpl
import me.bookk.feature.settings.domain.impl.UpdateNotificationSettingsImpl
import me.bookk.feature.settings.domain.impl.UpdateNotificationTokenImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun settingsDomainModule() = module {
    factoryOf(::DeleteExpiredPersonalDataExportFileImpl) bind
        DeleteExpiredPersonalDataExportFile::class
    factoryOf(::DownloadPersonalDataExportImpl) bind DownloadPersonalDataExport::class
    factoryOf(::GetPersonalDataExportImpl) bind GetPersonalDataExport::class
    factoryOf(::StartPersonalDataExportImpl) bind StartPersonalDataExport::class
    factoryOf(::AcceptLegalDocumentsImpl) bind AcceptLegalDocuments::class
    factoryOf(::GetLegalDocumentStatusesImpl) bind GetLegalDocumentStatuses::class
    factoryOf(::GetLegalDocumentVersionsImpl) bind GetLegalDocumentVersions::class
    factoryOf(::GetColorSchemeImpl) bind GetColorScheme::class
    factoryOf(::UpdateColorSchemeImpl) bind UpdateColorScheme::class
    factoryOf(::GetCrashReportingEnabledImpl) bind GetCrashReportingEnabled::class
    factoryOf(::UpdateCrashReportingEnabledImpl) bind UpdateCrashReportingEnabled::class
    factoryOf(::GetSettingsImpl) bind GetSettings::class
    factoryOf(::EditProfileImpl) bind EditProfile::class
    factoryOf(::DeleteAccountImpl) bind DeleteAccount::class
    factoryOf(::GetAvailablePasskeysImpl) bind GetAvailablePasskeys::class
    factoryOf(::DeletePasskeyImpl) bind DeletePasskey::class
    factoryOf(::CreateNewPasskeyImpl) bind CreateNewPasskey::class
    factoryOf(::GetNotificationSettingsImpl) bind GetNotificationSettings::class
    factoryOf(::UpdateNotificationSettingsImpl) bind UpdateNotificationSettings::class
    factoryOf(::UpdateNotificationTokenImpl) bind UpdateNotificationToken::class
}
