import SwiftUI
import shared

struct OptionsMultiPickerField<ItemView: View>: View {
    @Bindable private var state: IOSOptionsMultiPickerState

    private let itemContent: (PickerPresentation, @escaping () -> Void) -> ItemView

    @State private var isSheetPresented = false
    @State private var pickerArgs: PickerScreenArgs? = nil

    init(
        _ state: OptionsMultiPickerState,
        @ViewBuilder itemContent: @escaping (PickerPresentation, @escaping () -> Void) -> ItemView
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
			ForEach(state.selectedItems, id: \.pickerItemId) { item in
				itemContent(item) {
					withAnimation {
						state.onItemsRemoveRequested([item])
					}
				}
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
					NavigationStack {
						PickOptionScreen(args: args) { items in
							let pickedKeys = Set(items.map(\.key))
							withAnimation {
								state.onItemsPicked(state.options.filter { pickedKeys.contains($0.pickerItemId) })
							}
							pickerArgs = nil
						}
					}
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
        addItemText: RawStringDesc(string: "Add service")
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
