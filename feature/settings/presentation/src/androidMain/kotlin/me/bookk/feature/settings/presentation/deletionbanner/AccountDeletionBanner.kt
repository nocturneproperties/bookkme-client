package me.bookk.feature.settings.presentation.deletionbanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoDelete
import androidx.compose.runtime.Composable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import me.bookk.designsystem.components.AlertStrip
import me.bookk.designsystem.components.ObserveNotifications
import org.koin.androidx.compose.koinViewModel

@Composable
fun AccountDeletionBanner() {
    val viewModel: AccountDeletionBannerViewModel = koinViewModel()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.onAppResumed() }
    ObserveNotifications(viewModel.uiState.notifications)
    AccountDeletionBannerStrip(viewModel.uiState)
}

@Composable
private fun AccountDeletionBannerStrip(state: AccountDeletionBannerState) {
    AnimatedVisibility(
        visible = state.isVisible,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        AlertStrip(
            text = state.message,
            action = state.cancelButton,
            icon = Icons.Outlined.AutoDelete,
        )
    }
}
