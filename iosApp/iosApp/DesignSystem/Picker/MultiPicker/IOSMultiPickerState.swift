import shared
import SwiftUI

@Observable
@MainActor
class IOSMultiPickerState: IOSViewState, @MainActor MultiPickerState, NativeStateRepresentation {

    typealias SwiftType = IOSMultiPickerState
    typealias KotlinType = MultiPickerState

    var pickerTitle: any StringDesc
    var options: [PickerPresentation]
    var selectedItems: [PickerPresentation]
    var onItemsPicked: ([PickerPresentation]) -> Void
    var onItemsRemoveRequested: ([PickerPresentation]) -> Void
    var addItemText: any StringDesc
    var isEditable: Bool

    init(
        pickerTitle: any StringDesc = RawStringDesc(string: ""),
        options: [PickerPresentation] = [],
        selectedItems: [PickerPresentation] = [],
        onItemsPicked: @escaping ([PickerPresentation]) -> Void = { _ in },
        onItemsRemoveRequested: @escaping ([PickerPresentation]) -> Void = { _ in },
        addItemText: any StringDesc = RawStringDesc(string: ""),
        isEditable: Bool = true
    ) {
        self.pickerTitle = pickerTitle
        self.options = options
        self.selectedItems = selectedItems
        self.onItemsPicked = onItemsPicked
        self.onItemsRemoveRequested = onItemsRemoveRequested
        self.addItemText = addItemText
        self.isEditable = isEditable
    }

    func replaceOptions(options: [PickerPresentation]) {
        self.options = options
    }

    func replaceSelected(selected: [PickerPresentation]) {
        self.selectedItems = selected
    }
}

extension MultiPickerState {
    func impl() -> IOSMultiPickerState {
        return self as! IOSMultiPickerState
    }
}
