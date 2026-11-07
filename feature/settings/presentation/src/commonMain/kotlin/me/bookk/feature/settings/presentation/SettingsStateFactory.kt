package me.bookk.feature.settings.presentation

import me.bookk.feature.settings.presentation.accdelete.DeleteAccountState
import me.bookk.feature.settings.presentation.contactus.ContactUsState
import me.bookk.feature.settings.presentation.dashboard.SettingsState
import me.bookk.feature.settings.presentation.editprofile.EditProfileState
import me.bookk.feature.settings.presentation.export.PersonalDataExportState
import me.bookk.feature.settings.presentation.notifications.NotificationSettingsState
import me.bookk.feature.settings.presentation.passkey.PasskeyState
import me.bookk.feature.settings.presentation.privacy.PrivacySettingsState

interface SettingsStateFactory {
    fun createPersonalDataExportState(): PersonalDataExportState
    fun createSettingsState(initData: SettingsState.InitData): SettingsState
    fun createEditProfileState(initData: EditProfileState.InitData): EditProfileState
    fun createContactUsState(initData: ContactUsState.InitData): ContactUsState
    fun createDeleteAccountState(initData: DeleteAccountState.InitData): DeleteAccountState
    fun createPasskeyState(initData: PasskeyState.InitData): PasskeyState
    fun createNotificationSettingsState(): NotificationSettingsState
    fun createPrivacySettingsState(): PrivacySettingsState
}
