package me.bookk.feature.business.presentation.screen.plugins

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.simple.Action
import me.bookk.designsystem.uistate.simple.OptionalInfoLine

internal class AndroidBusinessPluginState : BusinessPluginState {

    override var title: StringDesc by mutableStateOf("".desc())
    override var subtitle: StringDesc by mutableStateOf("".desc())
    override var isEnabled: Boolean by mutableStateOf(false)
    override var isExpanded: Boolean by mutableStateOf(false)
    override val youCan: ListState<OptionalInfoLine> = AndroidListState()
    override val clientCan: ListState<OptionalInfoLine> = AndroidListState()
    override var demo: Action? by mutableStateOf(null)
    override val enable: ButtonState = AndroidButtonState()
}