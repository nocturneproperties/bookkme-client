package me.bookk.feature.settings.presentation

import me.bookk.designsystem.test.FakeNotificationState
import me.bookk.feature.settings.presentation.confirmaction.ConfirmActionState

internal class FakeClientSettingsStateFactory : ClientSettingsStateFactory {
    override fun createConfirmActionState(): ConfirmActionState {
        return FakeConfirmActionState()
    }
}

internal class FakeConfirmActionState : ConfirmActionState {
    override val notifications = FakeNotificationState()
}
