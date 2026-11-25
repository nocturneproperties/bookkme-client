package me.bookk.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import me.bookk.core.presentation.navigation.LocalIsCurrentScreen

@Composable
fun SendLifecycleEventsTo(viewModel: ViewModel) {
    val isCurrentScreen = LocalIsCurrentScreen.current
    DisposableEffect(viewModel, isCurrentScreen) {
        if (isCurrentScreen) viewModel.onViewPresented()
        onDispose {
            if (isCurrentScreen) viewModel.onViewHidden()
        }
    }
}
