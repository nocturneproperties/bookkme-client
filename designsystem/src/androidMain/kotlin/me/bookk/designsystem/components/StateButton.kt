package me.bookk.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.color.AppColors
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.ButtonState

@Composable
fun ActionButton(
    state: ButtonState,
    modifier: Modifier = Modifier,
    startIcon: Int? = null,
    endIcon: Int? = null,
    startContent: (@Composable () -> Unit)? = null,
    endContent: (@Composable () -> Unit)? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = LocalColors.current.ButtonActive,
        contentColor = AppColors.White,
        disabledContainerColor = LocalColors.current.ButtonInactive,
        disabledContentColor = LocalColors.current.ActionTextDisabled
    ),
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.heightIn(min = 48.dp),
        colors = colors,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        enabled = state.isEnabled,
        onClick = onClick
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                trackColor = Color.Transparent,
                color = colors.contentColor,
                modifier = Modifier
                    .height(24.dp)
                    .width(24.dp)
            )
        } else {
            startContent?.invoke()
            startIcon?.let { ButtonIcon(id = it) }
            Text(
                text = state.text.localized(),
                style = textStyle,
                textAlign = TextAlign.Center,
            )
            endIcon?.let { ButtonIcon(id = it) }
            endContent?.invoke()
        }
    }
}

@Composable
fun TextButton(
    state: ButtonState,
    modifier: Modifier = Modifier,
    startIcon: Int? = null,
    endIcon: Int? = null,
    startContent: (@Composable () -> Unit)? = null,
    endContent: (@Composable () -> Unit)? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = Color.Transparent,
        contentColor = LocalColors.current.ButtonActive,
        disabledContainerColor = Color.Transparent,
        disabledContentColor = LocalColors.current.ActionTextDisabled
    ),
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier.heightIn(min = 48.dp),
        colors = colors,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        enabled = state.isEnabled,
        onClick = onClick
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                trackColor = Color.Transparent,
                color = colors.contentColor,
                modifier = Modifier
                    .height(24.dp)
                    .width(24.dp)
            )
        } else {
            startContent?.invoke()
            startIcon?.let { ButtonIcon(id = it) }
            Text(
                text = state.text.localized(),
                style = textStyle,
                textAlign = TextAlign.Center,
            )
            endIcon?.let { ButtonIcon(id = it) }
            endContent?.invoke()
        }
    }
}

@Composable
private fun ButtonIcon(id: Int, contentDescription: String? = null) {
    Icon(painter = painterResource(id = id), contentDescription = contentDescription)
}

@Preview
@Composable
private fun PreviewDefault() {
    AppTheme {
        Column {
            ActionButton(state = AndroidButtonState(text = "Text Example".desc())) {}
            TextButton(state = AndroidButtonState(text = "Text Example".desc())) {}
        }
    }
}

@Preview
@Composable
private fun PreviewDisabled() {
    AppTheme {
        Column {
            ActionButton(
                state = AndroidButtonState(
                    text = "Text Example".desc(),
                    isEnabled = false
                )
            ) {}
            TextButton(
                state = AndroidButtonState(
                    text = "Text Example".desc(),
                    isEnabled = false
                )
            ) {}
        }
    }
}

@Preview
@Composable
private fun PreviewLoading() {
    AppTheme {
        Column {
            ActionButton(
                state = AndroidButtonState(
                    text = "Text Example".desc(),
                    isLoading = true
                )
            ) {}
            TextButton(
                state = AndroidButtonState(
                    text = "Text Example".desc(),
                    isLoading = true
                )
            ) {}
        }
    }
}