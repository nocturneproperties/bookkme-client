package me.bookk.designsystem.uistate

interface ViewState {
    var id: String
    var isVisible: Boolean

    fun refreshIdentity()
}