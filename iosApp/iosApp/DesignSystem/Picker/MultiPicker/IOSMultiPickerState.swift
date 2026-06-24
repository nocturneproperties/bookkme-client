import shared
import SwiftUI

@Observable
@MainActor
class IOSMultiPickerState: IOSViewState, @MainActor MultiPickerState, NativeStateRepresentation {

    typealias SwiftType = IOSMultiPickerState
    typealias KotlinType = MultiPickerState

    var pickerTitle: any StringDesc
    var placeholder: (any StringDesc)?
    var selectedItems: [PickerPresentation]
    var onItemsPicked: ([PickerPresentation]) -> Void
    var onItemsRemoveRequested: ([PickerPresentation]) -> Void
    var addItemButton: any ButtonState
    var isEditable: Bool
    var isPickerVisible: Bool

    init(
        pickerTitle: any StringDesc = RawStringDesc(string: ""),
        placeholder: (any StringDesc)? = nil,
        selectedItems: [PickerPresentation] = [],
        onItemsPicked: @escaping ([PickerPresentation]) -> Void = { _ in },
        onItemsRemoveRequested: @escaping ([PickerPresentation]) -> Void = { _ in },
        addItemButton: (any ButtonState)? = nil,
        isEditable: Bool = true,
        isPickerVisible: Bool = false
    ) {
        self.pickerTitle = pickerTitle
        self.placeholder = placeholder
        self.selectedItems = selectedItems
        self.onItemsPicked = onItemsPicked
        self.onItemsRemoveRequested = onItemsRemoveRequested
        self.addItemButton = addItemButton ?? IOSButtonState()
        self.isEditable = isEditable
        self.isPickerVisible = isPickerVisible
    }

    func replaceSelected(items: [PickerPresentation]) {
        self.selectedItems = items
    }
}

extension MultiPickerState {
    func impl() -> IOSMultiPickerState {
        return self as! IOSMultiPickerState
    }
}
