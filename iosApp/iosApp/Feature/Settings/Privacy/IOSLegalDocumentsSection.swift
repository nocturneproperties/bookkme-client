import SwiftUI
import shared

@Observable
@MainActor
class IOSLegalDocumentsSection: @MainActor LegalDocumentsSection, NativeStateRepresentation {
    typealias SwiftType = IOSLegalDocumentsSection
    typealias KotlinType = LegalDocumentsSection

    var title: any StringDesc = RawStringDesc(string: "")
    var documents: [LegalDocumentItem] = []
    var isLoading: Bool = false
    var message: (any StringDesc)? = nil
    let accept: any ButtonState = IOSButtonState()
    let retry: any ButtonState = IOSButtonState()
}
