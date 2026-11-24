package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.designsystem.uistate.RefreshState

class WebRefreshState : RefreshState {
    override var isRefreshing: Boolean by mutableStateOf(false)
    override var onRefresh: () -> Unit by mutableStateOf({})
}
