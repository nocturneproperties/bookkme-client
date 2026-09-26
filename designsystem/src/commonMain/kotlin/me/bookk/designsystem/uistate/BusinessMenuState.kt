package me.bookk.designsystem.uistate

import kotlin.uuid.Uuid

interface BusinessMenuState {
    var items: List<BusinessMenuItem>
    var selectedBusinessId: Uuid?
    var onBusinessClick: ((Uuid) -> Unit)?
    var onCreateClick: (() -> Unit)?
    var onJoinClick: (() -> Unit)?
}

data class BusinessMenuItem(val id: Uuid, val name: String)
