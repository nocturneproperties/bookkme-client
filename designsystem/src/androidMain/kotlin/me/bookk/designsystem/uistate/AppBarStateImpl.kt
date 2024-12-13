package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc

@Immutable
class AppBarStateImpl(
    title: StringDesc,
    subtitle: StringDesc?
): AppBarState {
    override var title: StringDesc by mutableStateOf(title)
    override var subtitle: StringDesc? by mutableStateOf(subtitle)
}