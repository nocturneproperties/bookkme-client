import shared

@Observable
@MainActor
class IOSChatConfirmationState: @MainActor ChatConfirmationState {
    var notice: (any StringDesc)?
    var isWriteAccessRequested: Bool = false
    var onWriteAccessAnswered: (KotlinBoolean) -> Void = { _ in }
}
