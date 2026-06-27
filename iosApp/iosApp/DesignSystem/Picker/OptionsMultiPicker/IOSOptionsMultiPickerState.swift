import shared
import SwiftUI

@Observable
@MainActor
class IOSOptionsMultiPickerState: IOSViewState, @MainActor OptionsMultiPickerState, NativeStateRepresentation {

    typealias SwiftType = IOSOptionsMultiPickerState
    typealias KotlinType = OptionsMultiPickerState

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

extension OptionsMultiPickerState {
    func impl() -> IOSOptionsMultiPickerState {
        return self as! IOSOptionsMultiPickerState
    }
}
