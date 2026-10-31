package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.core.presentation.error.ActionType

class AndroidAppBarActionState : AndroidViewState(), AppBarActionState {
    override var icon: ImageResource? by mutableStateOf(null)
    override var contentDescription: StringDesc by mutableStateOf("".desc())
    override var type: ActionType by mutableStateOf(ActionType.POSITIVE)
    override var isEnabled: Boolean by mutableStateOf(true)
    override var isLoading: Boolean by mutableStateOf(false)
    override var onClick: () -> Unit by mutableStateOf({})
}
