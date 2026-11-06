import SwiftUI
import shared

struct PrivacyView: View {

	let state: IOSPrivacySection

	init(state: PrivacySection) {
		self.state = state.impl()
	}

	var body: some View {
		Section {
			StateSwitch(state: state.crashReporting)
		} header: {
			Text(state.title.localized())
		} footer: {
			Text(state.crashReportingDescription.localized())
		}
	}
}
