package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.core.UsedInSwift

/**
 * Child class should be data class. Not using data class can cause inconsistencies when selectedItem
 * instance is the same by property values but the instance is not the same as in options list
 **/
abstract class PickerPresentation {
    abstract val pickerItemId: Long
    abstract val displayName: StringDesc
}

@UsedInSwift
data class MinimalPickerPresentation(
    override val pickerItemId: Long,
    override val displayName: StringDesc
) : PickerPresentation()

interface PickerFieldState<T : PickerPresentation> {
    var text: StringDesc
    val options: List<T>
    var selectedItem: T

    fun replaceOptions(options: List<T>)
}