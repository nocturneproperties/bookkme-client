package me.bookk.feature.settings.presentation.factory

import me.bookk.feature.settings.presentation.ClientSettingsStateFactory
import me.bookk.feature.settings.presentation.confirmaction.ConfirmActionState
import me.bookk.feature.settings.presentation.confirmaction.WebConfirmActionState

internal class WebClientSettingsStateFactory : ClientSettingsStateFactory {
    override fun createConfirmActionState(): ConfirmActionState {
        return WebConfirmActionState()
    }
}
