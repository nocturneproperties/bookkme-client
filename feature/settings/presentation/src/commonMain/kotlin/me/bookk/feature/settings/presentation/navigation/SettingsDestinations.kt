package me.bookk.feature.settings.presentation.navigation

import kotlinx.serialization.Serializable

sealed class SettingsDestination {
    @Serializable
    data object Dashboard : SettingsDestination()

    @Serializable
    data object EditProfile : SettingsDestination()

    @Serializable
    data object Passkey : SettingsDestination()

    @Serializable
    data object DeleteAccount : SettingsDestination()

    @Serializable
    data object Contact : SettingsDestination()

    @Serializable
    data object SuggestFeature : SettingsDestination()

    @Serializable
    data object Report : SettingsDestination()
}
