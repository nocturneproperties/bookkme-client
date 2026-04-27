//
//  IOSSettingsState.swift
//  iosApp
//
//  Created by BookkMe on 07.02.2025.
//  Copyright © 2025 BookkMe. All rights reserved.
//

import shared

@MainActor
class IOSSettingsState: @MainActor SettingsState {
	var account: any AccountSection
	
	var appearance: any AppearanceSection
	
	var profile: any ProfileSection
	
	var support: any SupportSection
	
	var notification: any PresentationNotificationState
	
	init(initData: SettingsStateInitData) {
		account = IOSAccountSection(initData: initData.account)
		appearance = IOSAppearanceSection(initData: initData.appearance)
		profile = IOSProfileSection(initData: initData.profile)
		support = IOSSupportSection(initData: initData.support)
		notification = IOSNotificationState()
	}
}

@MainActor
@Observable
class IOSAccountSection: @MainActor AccountSection {
	
	var title: any StringDesc
	
	var deleteAccount: any TextState
	
	var logout: any TextState
	
	var passkey: any TextState
	
	
	init(initData: AccountSectionInitData) {
		title = initData.title
		logout = IOSTextState(text: initData.logoutLabel)
		passkey = IOSTextState(text: initData.passkeyLabel)
		deleteAccount = IOSTextState(text: initData.deleteAccountLabel)
	}
}

extension AccountSection {
	func impl() -> IOSAccountSection {
		return self as! IOSAccountSection
	}
}

@Observable
@MainActor
class IOSAppearanceSection: @MainActor AppearanceSection {
	
	var title: any StringDesc
	var colorScheme: AppearanceSectionUIColorScheme
	
	
	init(initData: AppearanceSectionInitData) {
		title = initData.title
		colorScheme = AppearanceSectionUIColorScheme.system
	}
}

extension AppearanceSection {
	func impl() -> IOSAppearanceSection {
		return self as! IOSAppearanceSection
	}
}

@MainActor
@Observable
class IOSProfileSection: @MainActor ProfileSection {
	
	var title: any StringDesc
	var email: any StringDesc
	var lastName: any StringDesc
	var name: any StringDesc
	
	var editProfile: any ButtonState
	
	
	init(initData: ProfileSectionInitData) {
		title = initData.title
		editProfile = IOSButtonState(text: initData.editButtonText)
		email = RawStringDesc(string: "")
		lastName = RawStringDesc(string: "")
		name = RawStringDesc(string: "")
	}
}

extension ProfileSection {
	func impl() -> IOSProfileSection {
		return self as! IOSProfileSection
	}
}

@MainActor
@Observable
class IOSSupportSection: @MainActor SupportSection {
	var title: any StringDesc
	
	var contact: any TextState
	
	var feature: any TextState
	
	var policy: any TextState
	
	var reportError: any TextState
	
	var terms: any TextState
	
	
	init(initData: SupportSectionInitData) {
		title = initData.title
		contact = IOSTextState(text: initData.contactLabel)
		feature = IOSTextState(text: initData.featureLabel)
		policy = IOSTextState(text: initData.policyLabel)
		reportError = IOSTextState(text: initData.reportErrorLabel)
		terms = IOSTextState(text: initData.termsLabel)
	}
}

extension SupportSection {
	func impl() -> IOSSupportSection {
		return self as! IOSSupportSection
	}
}

