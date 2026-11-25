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
	
	var appBar: any AppBarState
	
	var account: any AccountSection
	var appearance: any AppearanceSection
	var profile: any ProfileSection
	var support: any SupportSection
	
	var notification: any PresentationNotificationState
	var navigation: any NavigationState
	
	init(initData: SettingsStateInitData) {
		appBar = IOSAppBarState()
		account = IOSAccountSection(initData: initData.account)
		appearance = IOSAppearanceSection(initData: initData.appearance)
		profile = IOSProfileSection()
		support = IOSSupportSection(initData: initData.support)
		notification = IOSNotificationState()
		navigation = IOSNavigationState()
	}
}

@MainActor
@Observable
class IOSAccountSection: @MainActor AccountSection {
	let dataExport: any ButtonState = IOSButtonState()
	let changeEmail: any TextState = IOSTextState(text: RawStringDesc(string: ""))
	let privacy: any ButtonState = IOSButtonState()
	var privacyStatus: (any StringDesc)? = nil
	
	var title: any StringDesc
	var deleteAccount: any ButtonState
	var logout: any TextState
	var passkey: any TextState
	var notifications: any ButtonState
	
	
	init(initData: AccountSectionInitData) {
		title = initData.title
		logout = IOSTextState(text: initData.logoutLabel)
		passkey = IOSTextState(text: initData.passkeyLabel)
		deleteAccount = IOSButtonState(text: initData.deleteAccountLabel)
		notifications = IOSButtonState(text: initData.notificationsLabel)
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
	
	var email: any StringDesc
	var fullName: any StringDesc
	var initials: String
	let edit: any ButtonState
	
	
	init() {
		email = RawStringDesc(string: "")
		fullName = RawStringDesc(string: "")
		initials = ""
		edit = IOSButtonState()
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
	
	var contactPage: any ButtonState
	
	var feature: any TextState
	
	var policy: any ButtonState
	
	var reportError: any TextState
	
	var terms: any ButtonState
	
	var onContactClick: () -> Void = {}
	
	
	init(initData: SupportSectionInitData) {
		title = initData.title
		contact = IOSTextState(text: initData.contactLabel)
		contactPage = IOSButtonState(text: initData.contactLabel)
		feature = IOSTextState(text: initData.featureLabel)
		policy = IOSButtonState(text: initData.policyLabel)
		reportError = IOSTextState(text: initData.reportErrorLabel)
		terms = IOSButtonState(text: initData.termsLabel)
	}
}

extension SupportSection {
	func impl() -> IOSSupportSection {
		return self as! IOSSupportSection
	}
}

