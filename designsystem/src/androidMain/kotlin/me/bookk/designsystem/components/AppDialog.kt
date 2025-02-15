package me.bookk.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.ButtonState

@Composable
fun AppDialog(
    title: String? = null,
    subtitle: String? = null,
    content: (@Composable () -> Unit)? = null,
    rightButton: ButtonState? = null,
    leftButton: ButtonState? = null,
    rightButtonColor: Color = LocalColors.current.actionText,
    leftButtonColor: Color = LocalColors.current.actionText,
    onRightButtonClicked: (() -> Unit)? = null,
    onLeftButtonClicked: (() -> Unit)? = null,
    onDismiss: () -> Unit,
) {
    AppDialogContainer(onDismiss) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        color = LocalColors.current.primaryText,
                    )
                }
                subtitle?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalColors.current.primaryText,
                    )
                }
                content?.invoke()
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leftButton?.let {
                    ActionButton(
                        onClick = { onLeftButtonClicked?.invoke() },
                        state = it,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = leftButtonColor,
                        )
                    )
                }
                rightButton?.let {
                    ActionButton(
                        onClick = { onRightButtonClicked?.invoke() },
                        state = it,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = rightButtonColor,
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun AppDialogContainer(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = LocalColors.current.elevated
            ),
            modifier = Modifier.widthIn(max = 312.dp)
        ) {
            content()
        }
    }
}

@Composable
fun AppDialogScreenContainer(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = LocalColors.current.elevated
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        AppDialog(
            title = "Title",
            subtitle = "Subtitle",
            rightButton = AndroidButtonState("Action".desc()),
            onDismiss = {}
        )
    }
}