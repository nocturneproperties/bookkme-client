package library.picker

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.json.Json
import me.bookk.core.presentation.navigation.serializableNavTypeEntry
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.PICKER_RESULT
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

fun NavGraphBuilder.pickScreen(controller: NavController) {
    composable<PickOptionDestination>(
        typeMap = mapOf(serializableNavTypeEntry<PickerScreenArgs>(false))
    ) {
        val destination: PickOptionDestination = it.toRoute()
        val viewModel: PickOptionViewModel = koinViewModel { parametersOf(destination.args) }
        PickOptionScreen(viewModel.uiState)
        ObserveNavigation(state = viewModel.uiState.navigation) { destination ->
            when (destination) {
                is PickerNavigationDestination.FinishWithResult -> {
                    val resultKey = PICKER_RESULT + destination.resultId
                    controller.previousBackStackEntry?.savedStateHandle?.set(resultKey, Json.encodeToString(destination.pickResult))
                    controller.popBackStack()
                }
            }
        }
    }
}