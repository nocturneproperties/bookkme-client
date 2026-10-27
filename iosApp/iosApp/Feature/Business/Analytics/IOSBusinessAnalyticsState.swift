import shared
import SwiftUI

@Observable
@MainActor
class IOSBusinessAnalyticsState: @MainActor BusinessAnalyticsState, NativeStateRepresentation {

	typealias SwiftType = IOSBusinessAnalyticsState

	typealias KotlinType = BusinessAnalyticsState

	let appBar: any AppBarState

	var title: any StringDesc
	var message: any StringDesc

	let navigation: any NavigationState
	let notifications: any PresentationNotificationState

	init() {
		appBar = IOSAppBarState()
		title = RawStringDesc(string: "")
		message = RawStringDesc(string: "")
		navigation = IOSNavigationState()
		notifications = IOSNotificationState()
	}
}
