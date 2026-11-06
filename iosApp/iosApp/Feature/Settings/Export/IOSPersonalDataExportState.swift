import shared
import SwiftUI

@Observable
@MainActor
class IOSPersonalDataExportState: @MainActor PersonalDataExportState, NativeStateRepresentation {
    typealias SwiftType = IOSPersonalDataExportState
    typealias KotlinType = PersonalDataExportState

    let appBar: any AppBarState = IOSAppBarState()
    var status = UIExportStatus(
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
    let refresh: any ButtonState = IOSButtonState()
    let notifications: any PresentationNotificationState = IOSNotificationState()
    let navigation: any NavigationState = IOSNavigationState()
}
