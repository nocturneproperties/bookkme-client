//
//  IOSSettingsStateFactory.swift
//  iosApp
//
//  Created by BookkMe on 07.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

@MainActor
class IOSSettingsStateFactory: @MainActor SettingsStateFactory {
	func createPersonalDataExportState() -> any PersonalDataExportState {
		return IOSPersonalDataExportState()
	}

	
	func createEditProfileState() -> any EditProfileState {
		return IOSEditProfileState()
	}
	
	func createSettingsState(initData: SettingsStateInitData) -> any SettingsState {
		return IOSSettingsState(initData: initData)
	}
	
	func createDeleteAccountState() -> any DeleteAccountState {
		return IOSDeleteAccountState()
	}

	func createAccountDeletionBannerState() -> any AccountDeletionBannerState {
		return IOSAccountDeletionBannerState()
	}
	
	func createPasskeyState(initData: PasskeyStateInitData) -> any PasskeyState {
		return IOSPasskeyState(initData: initData)
	}

	func createPrivacySettingsState() -> any PrivacySettingsState {
		return IOSPrivacySettingsState()
	}

	func createNotificationSettingsState() -> any NotificationSettingsState {
		return IOSNotificationSettingsState()
	}
}
