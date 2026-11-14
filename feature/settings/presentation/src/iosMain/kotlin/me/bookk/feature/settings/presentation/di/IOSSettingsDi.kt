package me.bookk.feature.settings.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.settings.presentation.accdelete.DeleteAccountViewModel
import me.bookk.feature.settings.presentation.dashboard.SettingsDashboardViewModel
import me.bookk.feature.settings.presentation.editprofile.EditProfileViewModel
import me.bookk.feature.settings.presentation.export.PersonalDataExportViewModel
import me.bookk.feature.settings.presentation.notifications.NotificationSettingsViewModel
import me.bookk.feature.settings.presentation.passkey.PasskeyViewModel
import me.bookk.feature.settings.presentation.privacy.PrivacySettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

internal actual fun platformSettingsDiModule(): Module = module {
    factoryOf(::SettingsDashboardViewModel)
    factoryOf(::EditProfileViewModel)
    factoryOf(::DeleteAccountViewModel)
    factoryOf(::PasskeyViewModel)
    factoryOf(::NotificationSettingsViewModel)
    factoryOf(::PersonalDataExportViewModel)
    factoryOf(::PrivacySettingsViewModel)
}

@UsedInSwift
fun settingsVM(): SettingsDashboardViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun editProfileVM(): EditProfileViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun deleteAccVM(): DeleteAccountViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun passkeyVM(): PasskeyViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun notificationSettingsVM(): NotificationSettingsViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun personalDataExportVM(): PersonalDataExportViewModel {
    return KoinPlatform.getKoin().get()
}

@UsedInSwift
fun privacySettingsVM(): PrivacySettingsViewModel {
    return KoinPlatform.getKoin().get()
}
