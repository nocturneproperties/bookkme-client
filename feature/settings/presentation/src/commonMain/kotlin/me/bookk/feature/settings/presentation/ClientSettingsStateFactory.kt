package me.bookk.feature.settings.presentation

import me.bookk.feature.settings.presentation.confirmaction.ConfirmActionState

interface ClientSettingsStateFactory {
    fun createConfirmActionState(): ConfirmActionState
}
