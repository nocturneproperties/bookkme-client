package me.bookk.feature.business.presentation.screen.link

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidRefreshState
import me.bookk.designsystem.uistate.AndroidTextFieldState

internal class AndroidBusinessLinkState : BusinessLinkState {
    override val appBar = AndroidAppBarState()
    override val name = AndroidTextFieldState()
    override var preview: StringDesc by mutableStateOf("".desc())
    override var loadError: StringDesc? by mutableStateOf(null)
    override val refresh = AndroidRefreshState()
    override val save = AndroidButtonState()
    override val share = AndroidButtonState()
    override val notifications = AndroidNotificationState()
    override val navigation = AndroidNavigationState<BusinessLinkDestinations>()
}
