import SwiftUI
import shared

@Observable
@MainActor
class IOSPriceAdjustmentSectionState: @MainActor PriceAdjustmentSectionState, NativeStateRepresentation {

	typealias SwiftType = IOSPriceAdjustmentSectionState
	typealias KotlinType = PriceAdjustmentSectionState

	var isVisible: Bool
	var reasonTitle: any StringDesc
	var reason: (any StringDesc)?
	let servicePicker: any OptionsMultiPickerState

	init() {
		isVisible = false
		reasonTitle = RawStringDesc(string: "")
		reason = nil
		servicePicker = IOSOptionsMultiPickerState()
	}
}
