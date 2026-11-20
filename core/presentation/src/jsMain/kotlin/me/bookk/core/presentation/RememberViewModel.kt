package me.bookk.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import org.koin.core.parameter.ParametersDefinition
import org.koin.mp.KoinPlatform

@Composable
inline fun <reified T : ViewModel> rememberViewModel(
    key: Any? = Unit,
    noinline parameters: ParametersDefinition? = null,
): T {
    val viewModel = remember(key) { KoinPlatform.getKoin().get<T>(parameters = parameters) }
    DisposableEffect(viewModel) {
        onDispose { viewModel.onCleared() }
    }
    return viewModel
}
