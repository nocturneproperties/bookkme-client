package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

@Immutable
class AndroidAppBarState(
    title: StringDesc = "".desc(),
    subtitle: StringDesc? = null,
    isVisible: Boolean = true,
    size: TopBarSize = TopBarSize.SMALL,
    onBackClick: (() -> Unit)? = null
) : AndroidViewState(isVisible), AppBarState {
    override var title: StringDesc by mutableStateOf(title)
    override var subtitle: StringDesc? by mutableStateOf(subtitle)
    override var onBackClick: (() -> Unit)? by mutableStateOf(onBackClick)
    override var size: TopBarSize by mutableStateOf(size)
    override val actions = AndroidListState<AppBarAction>()
}