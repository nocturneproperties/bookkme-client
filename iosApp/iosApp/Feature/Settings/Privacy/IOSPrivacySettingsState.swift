import shared

@Observable
@MainActor
class IOSPrivacySettingsState: @MainActor PrivacySettingsState, NativeStateRepresentation {
	typealias SwiftType = IOSPrivacySettingsState
	typealias KotlinType = PrivacySettingsState

	let appBar: any AppBarState = IOSAppBarState()
	var diagnosticsTitle: any StringDesc = RawStringDesc(string: "")
	let crashReporting: any BooleanState = IOSBooleanState()
	var crashReportingDescription: any StringDesc = RawStringDesc(string: "")
	let legal: any LegalDocumentsSection = IOSLegalDocumentsSection()
	let notifications: any PresentationNotificationState = IOSNotificationState()
	let navigation: any NavigationState = IOSNavigationState()
}
