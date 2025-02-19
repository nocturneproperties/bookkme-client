package me.bookk.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect

@Composable
fun SendLifecycleEventsTo(viewModel: ViewModel) {
    LaunchedEffect(key1 = null) {
        viewModel.onViewPresented()
    }
    DisposableEffect(key1 = null) {
        onDispose {
            viewModel.onViewHidden()
        }
    }
}