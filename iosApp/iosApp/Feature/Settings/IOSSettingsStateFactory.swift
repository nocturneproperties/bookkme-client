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

	
	func createEditProfileState(initData: EditProfileStateInitData) -> any EditProfileState {
		return IOSEditProfileState(initData: initData)
	}
	
	func createSettingsState(initData: SettingsStateInitData) -> any SettingsState {
		return IOSSettingsState(initData: initData)
	}
	
	func createDeleteAccountState(initData: DeleteAccountStateInitData) -> any DeleteAccountState {
		return IOSDeleteAccountState(initData: initData)
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
