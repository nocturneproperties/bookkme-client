package me.bookk.feature.settings.presentation

import me.bookk.feature.settings.presentation.dashboard.state.SettingsState

interface SettingsStateFactory {
    fun createSettingsState(initData: SettingsState.InitData): SettingsState
}