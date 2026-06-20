import SwiftUI
import shared

struct MultiPickerField<ItemContent: View, SheetContent: View>: View {
    @Bindable private var state: IOSOptionsMultiPickerState

	private let pickerContent: (IOSOptionsMultiPickerState) -> SheetContent
    private let itemContent: (PickerPresentation, @escaping () -> Void) -> ItemContent

    @State private var isSheetPresented = false

    init(
        _ state: OptionsMultiPickerState,
		@ViewBuilder pickerContent: @escaping (IOSOptionsMultiPickerState) -> SheetContent,
        @ViewBuilder itemContent: @escaping (PickerPresentation, @escaping () -> Void) -> ItemContent
    ) {
        self._state = Bindable(wrappedValue: state.impl())
        self.itemContent = itemContent
		self.pickerContent = pickerContent
    }

    var body: some View {
        if state.isVisible {
            VStack(alignment: .leading, spacing: 8) {
                Header(text: state.pickerTitle.localized())
                VStack(spacing: 0) {
                    ForEach(state.selectedItems, id: \.pickerItemId) { item in
                        itemContent(item) {
                            state.onItemsRemoveRequested([item])
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
                    }
                }
                .background(AppColors.elevated, in: RoundedRectangle(cornerRadius: 12))
                .animation(.easeInOut(duration: 0.2), value: state.selectedItems.count)
            }
            .sheet(isPresented: $isSheetPresented) {
				pickerContent(state)
            }
        }
    }
}
