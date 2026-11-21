import shared
import SwiftUI

@Observable
@MainActor
final class IOSPickedItem: @MainActor PickedItem {
	let item: PickerPresentation
	var quantity: Int32

	init(item: PickerPresentation, quantity: Int32 = 1) {
		self.item = item
		self.quantity = quantity
	}
}
