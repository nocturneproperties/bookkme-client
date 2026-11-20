import shared
import SwiftUI

@MainActor
@Observable
class IOSSignUpState: @MainActor SignUpState, NativeStateRepresentation {
    typealias SwiftType = IOSSignUpState
    typealias KotlinType = SignUpState

    let appBar: any AppBarState
    let email: any TextFieldState
    let lastName: any TextFieldState
    let name: any TextFieldState
    let confirmButton: any ButtonState
    let learnMoreButton: any ButtonState
    var passkeyInfoCardData: PasskeyInfoCardData
    let legalAcceptance: any BooleanState
    var legalDocuments: SignUpLegalDocumentsState? = nil
    let reloadLegalDocuments: any ButtonState
    let notification: any PresentationNotificationState
    let navigation: any NavigationState

    init() {
        appBar = IOSAppBarState()
        name = IOSTextFieldState()
        lastName = IOSTextFieldState()
        email = IOSTextFieldState()
        confirmButton = IOSButtonState(isEnabled: false)
        learnMoreButton = IOSButtonState()
        passkeyInfoCardData = PasskeyInfoCardData(
            title: RawStringDesc(string: ""),
            description: RawStringDesc(string: "")
        )
        legalAcceptance = IOSBooleanState()
        reloadLegalDocuments = IOSButtonState()
        notification = IOSNotificationState()
        navigation = IOSNavigationState()
    }
}

@MainActor
@Observable
class IOSSignUpLegalDocumentState: @MainActor SignUpLegalDocumentState, NativeStateRepresentation {
    typealias SwiftType = IOSSignUpLegalDocumentState
    typealias KotlinType = SignUpLegalDocumentState

    var document: ApiSignUpLegalDocument? = nil
    var linkText: any StringDesc = RawStringDesc(string: "")
    var linkUrl = ""
    var onLinkClick: (() -> Void)? = nil
}
