package me.bookk.feature.settings.presentation.export

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class PersonalDataExportDestinations : NavigationDestination() {
    data object Back : PersonalDataExportDestinations()
    data class Share(val filePath: String) : PersonalDataExportDestinations()
}
