package me.bookk.designsystem.test

import me.bookk.designsystem.uistate.ViewState

open class FakeViewState : ViewState {
    override var id: String = ""
    override var isVisible: Boolean = true
    var identityRefreshCount: Int = 0

    override fun refreshIdentity() {
        identityRefreshCount++
    }
}
