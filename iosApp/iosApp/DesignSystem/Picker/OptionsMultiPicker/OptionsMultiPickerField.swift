import SwiftUI
import shared

struct OptionsMultiPickerField<ItemView: View>: View {
    @Bindable private var state: IOSOptionsMultiPickerState

    private let itemContent: (PickerPresentation, @escaping () -> Void) -> ItemView

    @State private var isSheetPresented = false

    init(
        _ state: OptionsMultiPickerState,
        @ViewBuilder itemContent: @escaping (PickerPresentation, @escaping () -> Void) -> ItemView
    ) {
        self._state = Bindable(wrappedValue: state.impl())
        self.itemContent = itemContent
    }

    var body: some View {
        if state.isVisible {
			ForEach(state.selectedItems, id: \.pickerItemId) { item in
				itemContent(item) {
					withAnimation {
						state.onItemsRemoveRequested([item])
					}
				}
			}
			if state.isEditable {
				Button {
					isSheetPresented = true
				} label: {
					Text(state.addItemText.localized())
						.font(.callout)
						.fontWeight(.medium)
						.foregroundStyle(AppColors.actionText)
						.frame(maxWidth: .infinity, minHeight: 48, alignment: .leading)
						.padding(.horizontal, 16)
				}
				.buttonStyle(.plain)
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
			}
        }
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
            MinimalPickerPresentation(pickerItemId: "1", displayName: RawStringDesc(string: "Haircut")),
        ],
        addItemText: RawStringDesc(string: "+ Add service")
    )

    VStack {
        OptionsMultiPickerField(state) { item, onRemove in
            HStack {
                Text(item.displayName.localized())
                    .font(.body)
                Spacer()
                Button(action: onRemove) {
                    Image(systemName: "minus.circle")
                        .foregroundStyle(AppColors.error)
                }
                .buttonStyle(.plain)
            }
            .padding(.horizontal, 16)
            .frame(minHeight: 48)
        }
    }
    .padding(16)
    .background(AppColors.background)
}
