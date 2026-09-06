package me.bookk.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.simple.BannerErrorState

@Composable
fun BannerErrorView(state: BannerErrorState, modifier: Modifier = Modifier) {
    Row(
        modifier
            .fillMaxWidth()
            .background(LocalColors.current.error.copy(alpha = 0.12f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = LocalColors.current.error
        )
        Text(
            state.text.localized(),
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = LocalColors.current.error
        )
        Text(
            DesignSystem.strings.action_retry.desc().localized(),
            modifier = Modifier
                .clickable(onClick = state.onRetryClick)
                .padding(start = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = LocalColors.current.error
        )
    }
}
