package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

open class AndroidViewState(
    isVisible: Boolean
) : ViewState {
    override var isVisible: Boolean by mutableStateOf(isVisible)
}