import SwiftUI
import shared

struct MultiPickerField<ItemView: View>: View {
    @Bindable private var state: IOSMultiPickerState

    private let itemContent: (PickerPresentation, @escaping () -> Void) -> ItemView

    @State private var isSheetPresented = false

    init(
        _ state: MultiPickerState,
        @ViewBuilder itemContent: @escaping (PickerPresentation, @escaping () -> Void) -> ItemView
    ) {
        self._state = Bindable(wrappedValue: state.impl())
        self.itemContent = itemContent
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
                PickerBottomSheet(
                    title: state.pickerTitle.localized(),
                    options: state.options,
                    selectedId: nil,
                    onPick: { option in
                        state.onItemsPicked([option])
                        isSheetPresented = false
                    }
                )
            }
        }
    }
}

#Preview {
    @Previewable @State var state = IOSMultiPickerState(
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
        MultiPickerField(state) { item, onRemove in
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
