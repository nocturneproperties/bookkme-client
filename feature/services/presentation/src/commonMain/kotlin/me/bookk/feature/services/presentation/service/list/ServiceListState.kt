package me.bookk.feature.services.presentation.service.list

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.TextFieldState

interface ServiceListState {
    val appBar: AppBarState
    val searchField: TextFieldState
}