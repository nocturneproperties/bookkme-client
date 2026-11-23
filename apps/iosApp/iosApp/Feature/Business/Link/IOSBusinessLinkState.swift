import shared
import SwiftUI

@Observable
@MainActor
class IOSBusinessLinkState: @MainActor BusinessLinkState, NativeStateRepresentation {
    typealias SwiftType = IOSBusinessLinkState
    typealias KotlinType = BusinessLinkState

    let appBar: any AppBarState = IOSAppBarState()
    let name: any TextFieldState = IOSTextFieldState()
    var preview: any StringDesc = RawStringDesc(string: "")
    var loadError: (any StringDesc)?
    let refresh: any RefreshState = IOSRefreshState()
    let save: any ButtonState = IOSButtonState()
    let share: any ButtonState = IOSButtonState()
    let notifications: any PresentationNotificationState = IOSNotificationState()
    let navigation: any NavigationState = IOSNavigationState()
}
