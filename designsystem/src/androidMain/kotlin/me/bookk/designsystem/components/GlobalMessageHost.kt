package me.bookk.designsystem.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import me.bookk.core.presentation.error.PresentationNotification.GlobalMessage
import me.bookk.designsystem.colorToken
import me.bookk.designsystem.resources.color.themed
import me.bookk.designsystem.theme.color.LocalColors

@Composable
fun GlobalMessageHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    val currentData = hostState.currentSnackbarData
    LaunchedEffect(currentData) {
        val data = currentData ?: return@LaunchedEffect
        val millis = data.visuals.duration.toMillis() ?: return@LaunchedEffect
        delay(millis)
        data.dismiss()
    }
    AnimatedContent(
        targetState = currentData,
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter,
        transitionSpec = {
            slideInVertically { -it } togetherWith slideOutVertically { -it }
        },
        label = "GlobalMessage"
    ) { data ->
        if (data != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                GlobalMessagePill(data)
            }
        }
    }
}

@Composable
private fun GlobalMessagePill(data: SnackbarData) {
    val state = (data.visuals as? StatefulSnackbarVisuals)?.state ?: GlobalMessage.State.INFO
    val contentColor = LocalColors.current.onAction
    Row(
        modifier = Modifier
            .shadow(elevation = 8.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(state.colorToken().themed)
            .clickable { data.dismiss() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = state.icon(),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = data.visuals.message,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            modifier = Modifier.weight(1f, fill = false)
        )
        data.visuals.actionLabel?.let { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                modifier = Modifier.clickable { data.performAction() }
            )
        }
    }
}

private fun GlobalMessage.State.icon(): ImageVector {
    return when (this) {
        GlobalMessage.State.SUCCESS -> Icons.Outlined.CheckCircle
        GlobalMessage.State.ERROR -> Icons.Outlined.ErrorOutline
        GlobalMessage.State.INFO -> Icons.Outlined.Info
    }
}

private fun SnackbarDuration.toMillis(): Long? {
    return when (this) {
        SnackbarDuration.Short -> 4_000L
        SnackbarDuration.Long -> 10_000L
        SnackbarDuration.Indefinite -> null
    }
}
