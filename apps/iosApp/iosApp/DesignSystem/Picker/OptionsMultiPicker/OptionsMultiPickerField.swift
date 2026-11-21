import SwiftUI
import shared

struct OptionsMultiPickerField<ItemView: View>: View {
    @Bindable private var state: IOSOptionsMultiPickerState

    private let itemContent: (PickerPresentation, (() -> Void)?) -> ItemView

    @State private var isSheetPresented = false
    @State private var pickerArgs: PickerScreenArgs? = nil

    init(
        _ state: OptionsMultiPickerState,
        @ViewBuilder itemContent: @escaping (PickerPresentation, (() -> Void)?) -> ItemView
    ) {
        self._state = Bindable(wrappedValue: state.impl())
        self.itemContent = itemContent
    }

    var body: some View {
        if state.isVisible {
			if state.selectedItems.isEmpty, let placeholder = state.placeholder {
				Text(placeholder.localized())
					.font(.body)
					.foregroundStyle(AppColors.secondary)
					.multilineTextAlignment(.center)
					.frame(maxWidth: .infinity)
					.padding(.vertical, 16)
					.padding(.horizontal, 16)
					.alignmentGuide(.listRowSeparatorLeading) { d in d[.leading] }
					.alignmentGuide(.listRowSeparatorTrailing) { d in d[.trailing] }
			}
			ForEach(state.selectedItems, id: \.item.pickerItemId) { picked in
				pickedRow(picked)
			}
			if state.isEditable {
				Button {
					openPicker()
				} label: {
					HStack {
						Image(resource: DesignSystem.images().plus)
						Text(state.addItemText.localized())
					}
					.frame(maxWidth: .infinity, minHeight: 48)
					.contentShape(Rectangle())
				}
				.buttonStyle(.textInList)
				.sheet(isPresented: $isSheetPresented) {
					PickerBottomSheet(
						title: state.pickerTitle.localized(),
						options: state.options,
						selectedId: nil,
						onPick: { option in
							withAnimation {
								state.onItemsPicked([option])
								isSheetPresented = false
							}
						}
					)
				}
				.fullScreenCover(item: $pickerArgs) { args in
					OptionsMultiPickerScreen(args: args, options: state.options) { items in
						withAnimation {
							state.onItemsPicked(items)
						}
						pickerArgs = nil
					}
				}
			}
        }
    }

	@ViewBuilder
	private func pickedRow(_ picked: any PickedItem) -> some View {
		if state.isEditable && state.isQuantityEditable {
			HStack(spacing: 0) {
				itemContent(picked.item, nil)
				PickedQuantityStepper(state: state, picked: picked)
					.padding(.trailing, 8)
			}
		} else {
			itemContent(picked.item) {
				withAnimation {
					state.onItemsRemoveRequested([picked.item])
				}
			}
		}
	}

	private func openPicker() {
		switch state.pickerType {
		case .screen:
			pickerArgs = PickerScreenArgs.from(
				id: state.id,
				title: state.pickerTitle,
				options: state.options,
				choice: .multiple
			)
		default:
			isSheetPresented = true
		}
	}
}

private struct PickedQuantityStepper: View {
	let state: IOSOptionsMultiPickerState
	let picked: any PickedItem

	var body: some View {
		QuantityStepper(
			quantity: picked.quantity,
			onIncrease: { withAnimation { state.onQuantityIncreaseRequested(picked.item) } },
			onDecrease: { withAnimation { state.onQuantityDecreaseRequested(picked.item) } }
		)
	}
}

#Preview {
    @Previewable @State var state = IOSOptionsMultiPickerState(
        pickerTitle: RawStringDesc(string: "Services"),
        options: [
            MinimalPickerPresentation(pickerItemId: "1", displayName: RawStringDesc(string: "Haircut")),
            MinimalPickerPresentation(pickerItemId: "2", displayName: RawStringDesc(string: "Beard trim")),
            MinimalPickerPresentation(pickerItemId: "3", displayName: RawStringDesc(string: "Shampoo")),
        ],
        selectedItems: [
            IOSPickedItem(
                item: MinimalPickerPresentation(pickerItemId: "1", displayName: RawStringDesc(string: "Haircut")),
                quantity: 2
            ),
        ],
        addItemText: RawStringDesc(string: "Add service"),
        isQuantityEditable: true
    )

    VStack {
        OptionsMultiPickerField(state) { item, onRemove in
            HStack {
                Text(item.displayName.localized())
                    .font(.body)
                Spacer()
                if let onRemove {
                    Button(action: onRemove) {
                        Image(systemName: "minus.circle")
                            .foregroundStyle(AppColors.error)
                    }
                    .buttonStyle(.plain)
                }
            }
            .padding(.horizontal, 16)
            .frame(minHeight: 48)
        }
    }
    .padding(16)
    .background(AppColors.background)
}
