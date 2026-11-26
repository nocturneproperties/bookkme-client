package me.bookk.feature.settings.presentation.di

import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.dashboard.SettingsDashboardViewModel
import me.bookk.feature.settings.presentation.editprofile.EditProfileViewModel
import me.bookk.feature.settings.presentation.export.PersonalDataExportViewModel
import me.bookk.feature.settings.presentation.factory.WebSettingsStateFactory
import me.bookk.feature.settings.presentation.notifications.NotificationSettingsViewModel
import me.bookk.feature.settings.presentation.privacy.PrivacySettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformSettingsDiModule(): Module = module {
    factoryOf(::WebSettingsStateFactory) bind SettingsStateFactory::class
    factoryOf(::SettingsDashboardViewModel)
    factoryOf(::EditProfileViewModel)
    factoryOf(::NotificationSettingsViewModel)
    factoryOf(::PrivacySettingsViewModel)
    factoryOf(::PersonalDataExportViewModel)
}
