package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc

@Immutable
class OptionStateImpl(
    override val id: String,
    title: StringDesc,
    isSelected: Boolean,
) : OptionState {
    override var title: StringDesc by mutableStateOf(title)
    override var isSelected: Boolean by mutableStateOf(isSelected)
}