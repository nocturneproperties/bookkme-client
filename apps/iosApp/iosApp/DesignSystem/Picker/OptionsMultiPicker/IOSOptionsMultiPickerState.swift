import shared
import SwiftUI

@Observable
@MainActor
class IOSOptionsMultiPickerState: IOSViewState, @MainActor OptionsMultiPickerState, NativeStateRepresentation {

    typealias SwiftType = IOSOptionsMultiPickerState
    typealias KotlinType = OptionsMultiPickerState

    var pickerTitle: any StringDesc
    var pickerType: PickerFieldStatePickerType
    var placeholder: (any StringDesc)?
    var options: [PickerPresentation]
    var selectedItems: [any PickedItem]
    var onItemsPicked: ([PickerPresentation]) -> Void
    var onItemsRemoveRequested: ([PickerPresentation]) -> Void
    var onQuantityIncreaseRequested: (PickerPresentation) -> Void
    var onQuantityDecreaseRequested: (PickerPresentation) -> Void
    var addItemText: any StringDesc
    var isEditable: Bool
    var isQuantityEditable: Bool

    init(
        pickerTitle: any StringDesc = RawStringDesc(string: ""),
        pickerType: PickerFieldStatePickerType = PickerFieldStatePickerType.bottomSheet,
        placeholder: (any StringDesc)? = nil,
        options: [PickerPresentation] = [],
        selectedItems: [any PickedItem] = [],
        onItemsPicked: @escaping ([PickerPresentation]) -> Void = { _ in },
        onItemsRemoveRequested: @escaping ([PickerPresentation]) -> Void = { _ in },
        onQuantityIncreaseRequested: @escaping (PickerPresentation) -> Void = { _ in },
        onQuantityDecreaseRequested: @escaping (PickerPresentation) -> Void = { _ in },
        addItemText: any StringDesc = RawStringDesc(string: ""),
        isEditable: Bool = true,
        isQuantityEditable: Bool = false
    ) {
        self.pickerTitle = pickerTitle
        self.pickerType = pickerType
        self.placeholder = placeholder
        self.options = options
        self.selectedItems = selectedItems
        self.onItemsPicked = onItemsPicked
        self.onItemsRemoveRequested = onItemsRemoveRequested
        self.onQuantityIncreaseRequested = onQuantityIncreaseRequested
        self.onQuantityDecreaseRequested = onQuantityDecreaseRequested
        self.addItemText = addItemText
        self.isEditable = isEditable
        self.isQuantityEditable = isQuantityEditable
    }

    func createPickedItem(item: PickerPresentation, quantity: Int32) -> any PickedItem {
        IOSPickedItem(item: item, quantity: quantity)
    }

    func replaceOptions(options: [PickerPresentation]) {
        self.options = options
    }

    func replaceSelected(selected: [any PickedItem]) {
        self.selectedItems = selected
    }
}

extension OptionsMultiPickerState {
    func impl() -> IOSOptionsMultiPickerState {
        return self as! IOSOptionsMultiPickerState
    }
}
