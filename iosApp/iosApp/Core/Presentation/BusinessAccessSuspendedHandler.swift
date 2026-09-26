import Foundation

@Observable
class BusinessAccessSuspendedHandler: ObservableObject {
	var onBusinessAccessSuspended: () -> Void

	init(onBusinessAccessSuspended: @escaping () -> Void) {
		self.onBusinessAccessSuspended = onBusinessAccessSuspended
	}
}
