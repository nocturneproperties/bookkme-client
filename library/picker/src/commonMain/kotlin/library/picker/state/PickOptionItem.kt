package library.picker.state

import dev.icerock.moko.resources.desc.image.ImageDesc
import me.bookk.core.domain.entity.KeyValueData
import me.bookk.designsystem.uistate.CheckBoxState
import me.bookk.designsystem.uistate.ViewState

interface PickOptionItem : ViewState {
    var identity: KeyValueData
    var icon: ImageDesc?
    val checkBox: CheckBoxState
}