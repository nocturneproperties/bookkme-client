package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc

class AndroidSwitchState(
    text: StringDesc,
    isChecked: Boolean,
    isVisible: Boolean = true
) : AndroidViewState(isVisible), SwitchState {

    override var text: StringDesc by mutableStateOf(text)
    override var isChecked: Boolean by mutableStateOf(isChecked)
}