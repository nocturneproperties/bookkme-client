package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.core.UsedInSwift

/**
 * Child class should be data class. Not using data class can cause inconsistencies when selectedItem
 * instance is the same by property values but the instance is not the same as in options list
 **/
abstract class PickerPresentation {
    abstract val pickerItemId: String
    abstract val displayName: StringDesc
    open val displayIconUrl: String? = null

    abstract override fun hashCode(): Int
    abstract override fun equals(other: Any?): Boolean
}

@UsedInSwift
data class MinimalPickerPresentation(
    override val pickerItemId: String,
    override val displayName: StringDesc,
) : PickerPresentation()

@UsedInSwift
data class SimplePickerPresentation<T>(
    override val pickerItemId: String,
    override val displayName: StringDesc,
    val domain: T
) : PickerPresentation()

interface PickerFieldState<T : PickerPresentation> : ViewState {
    val textField: TextFieldState
    var pickerType: PickerType
    var pickerTitle: StringDesc
    val options: List<T>
    var selectedItem: T?
    var onItemPicked: (T?) -> Unit

    fun replaceOptions(options: List<T>)

    enum class PickerType {
        BOTTOM_SHEET,
        SCREEN
    }

}