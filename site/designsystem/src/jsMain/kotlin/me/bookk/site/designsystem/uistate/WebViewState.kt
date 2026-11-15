package me.bookk.site.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.designsystem.uistate.ViewState
import kotlin.uuid.Uuid

open class WebViewState(
    isVisible: Boolean = true,
) : ViewState {
    override var id: String by mutableStateOf(Uuid.random().toHexString())
    override var isVisible: Boolean by mutableStateOf(isVisible)

    override fun refreshIdentity() {
        id = Uuid.random().toHexString()
    }
}
