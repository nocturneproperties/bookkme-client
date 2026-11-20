import SwiftUI
import shared

struct OptionsMultiPickerScreen: View {
	let args: PickerScreenArgs
	let options: [PickerPresentation]
	let onItemsPicked: ([PickerPresentation]) -> Void

	var body: some View {
		NavigationStack {
			PickOptionScreen(args: args) { items in
				let pickedKeys = Set(items.map(\.key))
				onItemsPicked(options.filter { pickedKeys.contains($0.pickerItemId) })
			}
		}
	}
}
