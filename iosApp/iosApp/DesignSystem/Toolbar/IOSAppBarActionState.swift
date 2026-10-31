import shared

@Observable
@MainActor
final class IOSAppBarActionState: @MainActor IOSViewState, @MainActor AppBarActionState {

	var icon: ImageResource?
	var contentDescription: any StringDesc
	var type: ActionType
	var isEnabled: Bool
	var isLoading: Bool
	var onClick: () -> Void

	init() {
		self.icon = nil
		self.contentDescription = RawStringDesc(string: "")
		self.type = .positive
		self.isEnabled = true
		self.isLoading = false
		self.onClick = {}
		super.init(isVisible: true)
	}
}
