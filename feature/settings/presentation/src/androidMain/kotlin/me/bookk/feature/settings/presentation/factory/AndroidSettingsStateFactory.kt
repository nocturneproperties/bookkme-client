package me.bookk.feature.settings.presentation.factory

import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.dashboard.AndroidDashboardState
import me.bookk.feature.settings.presentation.dashboard.state.SettingsState
import me.bookk.feature.settings.presentation.editprofile.AndroidEditProfileState
import me.bookk.feature.settings.presentation.editprofile.state.EditProfileState

class AndroidSettingsStateFactory : SettingsStateFactory {
    override fun createSettingsState(initData: SettingsState.InitData): SettingsState {
        return AndroidDashboardState(initData = initData)
    }

    override fun createEditProfileState(initData: EditProfileState.InitData): EditProfileState {
        return AndroidEditProfileState(initData)
    }
}