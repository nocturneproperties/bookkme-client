package me.bookk.feature.settings.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.settings.presentation.contactus.ContactUsViewModel
import me.bookk.feature.settings.presentation.dashboard.SettingsDashboardViewModel
import me.bookk.feature.settings.presentation.editprofile.EditProfileViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

internal actual fun platformSettingsDiModule(): Module = module {
    factoryOf(::SettingsDashboardViewModel)
    factoryOf(::EditProfileViewModel)
    factoryOf(::ContactUsViewModel)
}

@UsedInSwift
fun settingsVM(): SettingsDashboardViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun editProfileVM(): EditProfileViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun contactUsVM():ContactUsViewModel = KoinPlatform.getKoin().get()