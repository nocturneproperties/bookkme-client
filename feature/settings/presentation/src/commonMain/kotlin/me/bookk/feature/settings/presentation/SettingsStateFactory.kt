package me.bookk.feature.settings.presentation

import me.bookk.feature.settings.presentation.dashboard.state.SettingsState
import me.bookk.feature.settings.presentation.editprofile.state.EditProfileState

interface SettingsStateFactory {
    fun createSettingsState(initData: SettingsState.InitData): SettingsState
    fun createEditProfileState(initData: EditProfileState.InitData): EditProfileState
}