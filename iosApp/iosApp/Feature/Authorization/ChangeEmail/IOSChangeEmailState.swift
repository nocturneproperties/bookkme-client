import shared
import SwiftUI

@MainActor
@Observable
class IOSChangeEmailState: @MainActor ChangeEmailState, NativeStateRepresentation {
    typealias SwiftType = IOSChangeEmailState
    typealias KotlinType = ChangeEmailState

    let appBar: any AppBarState
    var explanation: any StringDesc = RawStringDesc(string: "")
    let email: any TextFieldState
    let continueButton: any ButtonState
    let notifications: any PresentationNotificationState
    let navigation: any NavigationState

    init() {
        appBar = IOSAppBarState()
        email = IOSTextFieldState()
        continueButton = IOSButtonState(isEnabled: false)
        notifications = IOSNotificationState()
        navigation = IOSNavigationState()
    }
}
