package me.bookk.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.simple.ErrorState

@Composable
fun LazyItemScope.ErrorStateView(state: ErrorState, modifier: Modifier = Modifier) {
    Column(
        modifier.fillParentMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = LocalColors.current.error
        )
        Text(
            state.errorText.localized(),
            modifier = Modifier.padding(top = 24.dp),
            style = MaterialTheme.typography.bodyMedium.secondary()
        )
        val retryButtonState = remember(state) {
            AndroidButtonState(
                text = DesignSystem.strings.action_retry.desc(),
                onClick = state.onRetryClick
            )
        }
        StateTextButton(
            state = retryButtonState,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
