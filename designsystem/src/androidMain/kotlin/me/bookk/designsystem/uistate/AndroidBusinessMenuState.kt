package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.uuid.Uuid

class AndroidBusinessMenuState : BusinessMenuState {
    override var items: List<BusinessMenuItem> by mutableStateOf(emptyList())
    override var selectedBusinessId: Uuid? by mutableStateOf(null)
    override var onBusinessClick: ((Uuid) -> Unit)? by mutableStateOf(null)
    override var onCreateClick: (() -> Unit)? by mutableStateOf(null)
    override var onJoinClick: (() -> Unit)? by mutableStateOf(null)
}
