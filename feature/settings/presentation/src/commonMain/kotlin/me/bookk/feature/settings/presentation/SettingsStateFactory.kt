package me.bookk.feature.settings.presentation

import me.bookk.feature.settings.presentation.contactus.ContactUsState
import me.bookk.feature.settings.presentation.dashboard.SettingsState
import me.bookk.feature.settings.presentation.editprofile.EditProfileState

interface SettingsStateFactory {
    fun createSettingsState(initData: SettingsState.InitData): SettingsState
    fun createEditProfileState(initData: EditProfileState.InitData): EditProfileState
    fun createContactUsState(initData: ContactUsState.InitData): ContactUsState
}