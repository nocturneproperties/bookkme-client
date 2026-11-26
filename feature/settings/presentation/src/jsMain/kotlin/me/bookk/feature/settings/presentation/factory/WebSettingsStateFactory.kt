package me.bookk.feature.settings.presentation.factory

import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.accdelete.DeleteAccountState
import me.bookk.feature.settings.presentation.accdelete.WebDeleteAccountState
import me.bookk.feature.settings.presentation.dashboard.SettingsState
import me.bookk.feature.settings.presentation.dashboard.WebSettingsState
import me.bookk.feature.settings.presentation.editprofile.EditProfileState
import me.bookk.feature.settings.presentation.editprofile.WebEditProfileState
import me.bookk.feature.settings.presentation.export.PersonalDataExportState
import me.bookk.feature.settings.presentation.export.WebPersonalDataExportState
import me.bookk.feature.settings.presentation.notifications.NotificationSettingsState
import me.bookk.feature.settings.presentation.notifications.WebNotificationSettingsState
import me.bookk.feature.settings.presentation.passkey.PasskeyState
import me.bookk.feature.settings.presentation.privacy.PrivacySettingsState
import me.bookk.feature.settings.presentation.privacy.WebPrivacySettingsState

internal class WebSettingsStateFactory : SettingsStateFactory {
    override fun createPersonalDataExportState(): PersonalDataExportState {
        return WebPersonalDataExportState()
    }

    override fun createSettingsState(initData: SettingsState.InitData): SettingsState {
        return WebSettingsState(initData)
    }

    override fun createEditProfileState(): EditProfileState {
        return WebEditProfileState()
    }

    override fun createDeleteAccountState(): DeleteAccountState {
        return WebDeleteAccountState()
    }

    override fun createPasskeyState(initData: PasskeyState.InitData): PasskeyState {
        throw UnsupportedOperationException("Passkeys are not managed in the client web app")
    }

    override fun createNotificationSettingsState(): NotificationSettingsState {
        return WebNotificationSettingsState()
    }

    override fun createPrivacySettingsState(): PrivacySettingsState {
        return WebPrivacySettingsState()
    }
}
