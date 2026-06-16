package library.picker

import me.bookk.core.domain.entity.KeyValueData
import me.bookk.core.presentation.navigation.NavigationDestination

sealed class PickerNavigationDestination : NavigationDestination() {
    data object Back : PickerNavigationDestination()
    data class FinishWithResult(
        val resultId: String,
        val pickResult: KeyValueData
    ) : PickerNavigationDestination()
}