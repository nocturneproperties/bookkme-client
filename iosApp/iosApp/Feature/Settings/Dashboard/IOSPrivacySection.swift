import shared

@Observable
@MainActor
class IOSPrivacySection: @MainActor PrivacySection {
	var title: any StringDesc = RawStringDesc(string: "")
	var crashReportingDescription: any StringDesc = RawStringDesc(string: "")
	let crashReporting: any BooleanState = IOSBooleanState()
}

extension PrivacySection {
	func impl() -> IOSPrivacySection {
		return self as! IOSPrivacySection
	}
}
