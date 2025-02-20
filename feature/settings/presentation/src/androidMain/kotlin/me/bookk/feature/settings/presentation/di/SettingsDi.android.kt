package me.bookk.feature.settings.presentation.di

import me.bookk.feature.settings.presentation.contactus.ContactUsViewModel
import me.bookk.feature.settings.presentation.dashboard.SettingsDashboardViewModel
import me.bookk.feature.settings.presentation.editprofile.EditProfileViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal actual fun platformSettingsDiModule(): Module = module {
    viewModelOf(::SettingsDashboardViewModel)
    viewModelOf(::EditProfileViewModel)
    viewModelOf(::ContactUsViewModel)
}