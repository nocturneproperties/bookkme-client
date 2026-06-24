package library.picker.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.image.ImageDesc
import me.bookk.core.domain.entity.KeyValueData
import me.bookk.designsystem.uistate.AndroidBooleanState
import me.bookk.designsystem.uistate.AndroidViewState
import me.bookk.designsystem.uistate.BooleanState

class AndroidPickOptionItem : AndroidViewState(true), PickOptionItem {
    override var identity: KeyValueData by mutableStateOf(KeyValueData("", ""))
    override var icon: ImageDesc? by mutableStateOf(null)
    override val checkBox: BooleanState = AndroidBooleanState()
}