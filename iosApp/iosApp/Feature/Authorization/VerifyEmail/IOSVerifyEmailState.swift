import shared
import SwiftUI

@MainActor
@Observable
class IOSVerifyEmailState: @MainActor VerifyEmailState, NativeStateRepresentation {
    typealias SwiftType = IOSVerifyEmailState
    typealias KotlinType = VerifyEmailState

    let appBar: any AppBarState
    var codeSentMessage: any StringDesc = RawStringDesc(string: "")
    let code: any TextFieldState
    let confirmButton: any ButtonState
    let resendButton: any ButtonState
    let changeEmailButton: any ButtonState
    let notifications: any PresentationNotificationState
    let navigation: any NavigationState

    init() {
        appBar = IOSAppBarState()
        code = IOSTextFieldState()
        confirmButton = IOSButtonState(isEnabled: false)
        resendButton = IOSButtonState(isEnabled: false)
        changeEmailButton = IOSButtonState()
        notifications = IOSNotificationState()
        navigation = IOSNavigationState()
    }
}
