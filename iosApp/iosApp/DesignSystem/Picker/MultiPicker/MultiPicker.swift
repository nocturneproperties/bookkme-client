import SwiftUI
import shared

struct MultiPicker<ItemContent: View, SheetContent: View>: View {
    @Bindable private var state: IOSMultiPickerState

    private let pickerContent: () -> SheetContent
    private let itemContent: (PickerPresentation, @escaping () -> Void) -> ItemContent

    init(
        _ state: MultiPickerState,
        @ViewBuilder pickerContent: @escaping () -> SheetContent,
        @ViewBuilder itemContent: @escaping (PickerPresentation, @escaping () -> Void) -> ItemContent
    ) {
        self._state = Bindable(wrappedValue: IOSMultiPickerState.cast(state))
        self.pickerContent = pickerContent
        self.itemContent = itemContent
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            VStack(spacing: 0) {
                if state.selectedItems.isEmpty, let placeholder = state.placeholder {
                    Text(placeholder.localized())
                        .font(.body)
                        .foregroundStyle(AppColors.secondary)
                        .multilineTextAlignment(.center)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 16)
                }
                ForEach(state.selectedItems, id: \.pickerItemId) { item in
                    itemContent(item) {
						withAnimation {
							state.onItemsRemoveRequested([item])
						}
                    }
                }
                if state.isEditable {
                    TextButton(state.addItemButton) {
                        state.isPickerVisible = true
					}
					.buttonStyle(.textStandalone)
                }
            }
            .background(AppColors.elevated, in: RoundedRectangle(cornerRadius: 12))
            .animation(.easeInOut(duration: 0.2), value: state.selectedItems.count)
        }
        .sheet(isPresented: Binding(
            get: { state.isPickerVisible },
            set: { state.isPickerVisible = $0 }
        )) {
            pickerContent()
        }
    }
}
