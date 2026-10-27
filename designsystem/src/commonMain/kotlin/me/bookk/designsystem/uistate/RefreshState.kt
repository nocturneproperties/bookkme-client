package me.bookk.designsystem.uistate

interface RefreshState {
    var isRefreshing: Boolean
    var onRefresh: () -> Unit
}