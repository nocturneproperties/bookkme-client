import shared
import SwiftUI

@Observable
@MainActor
class IOSBusinessDataExportState: @MainActor BusinessDataExportState, NativeStateRepresentation {
    typealias SwiftType = IOSBusinessDataExportState
    typealias KotlinType = BusinessDataExportState

    let appBar: any AppBarState = IOSAppBarState()
    var status = UIBusinessExportStatus(
        title: RawStringDesc(string: ""),
        message: nil,
        footer: nil,
        icon: nil,
        color: .actiontext,
        isInProgress: false,
        showsFile: false
    )
    var expiry: (any StringDesc)?
    var requestAvailability: (any StringDesc)?
    var error: (any StringDesc)?
    let request: any ButtonState = IOSButtonState()
    let download: any ButtonState = IOSButtonState()
    let retry: any ButtonState = IOSButtonState()
    let notifications: any PresentationNotificationState = IOSNotificationState()
    let navigation: any NavigationState = IOSNavigationState()
}
