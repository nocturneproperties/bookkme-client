package me.bookk.feature.settings.presentation.factory

import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.contactus.AndroidContactUsState
import me.bookk.feature.settings.presentation.contactus.ContactUsState
import me.bookk.feature.settings.presentation.dashboard.AndroidDashboardState
import me.bookk.feature.settings.presentation.dashboard.SettingsState
import me.bookk.feature.settings.presentation.editprofile.AndroidEditProfileState
import me.bookk.feature.settings.presentation.editprofile.EditProfileState

class AndroidSettingsStateFactory : SettingsStateFactory {
    override fun createSettingsState(initData: SettingsState.InitData): SettingsState {
        return AndroidDashboardState(initData = initData)
    }

    override fun createEditProfileState(initData: EditProfileState.InitData): EditProfileState {
        return AndroidEditProfileState(initData)
    }

    override fun createContactUsState(initData: ContactUsState.InitData): ContactUsState {
        return AndroidContactUsState(initData)
    }
}