package me.bookk.feature.business.presentation.screen.export

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class BusinessDataExportDestinations : NavigationDestination() {
    data object Back : BusinessDataExportDestinations()
    data class Share(
        val filePath: String,
    ) : BusinessDataExportDestinations()
}
