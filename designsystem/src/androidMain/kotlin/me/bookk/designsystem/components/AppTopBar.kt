package me.bookk.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.text.AppTypography

val DefaultTopBarHeight = 44.dp

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    startContent: @Composable (() -> Unit)? = null,
    centerContent: @Composable (() -> Unit)? = null,
    endContent: @Composable (() -> Unit)? = null,
    containerColor: Color = LocalColors.current.Background,
    bottomLineColor: Color = LocalColors.current.Divider,
) {
    Column {
        Row(
            modifier = modifier
                .background(containerColor)
                .statusBarsPadding()
                .height(DefaultTopBarHeight)
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                startContent?.invoke()
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                centerContent?.invoke()
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                endContent?.invoke()
            }
        }

        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = bottomLineColor,
            thickness = 0.5.dp
        )
    }
}

@Composable
fun AppBarTitle(text: String) {
    Text(
        text = text,
        style = AppTypography.title3SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun AppBarNavButton(onClick: () -> Unit) {
    ClickableIcon(
        icon = Icons.AutoMirrored.Filled.ArrowBack,
        onClick = onClick
    )
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        AppTopBar(
            startContent = {
                AppBarNavButton {}
            },
            centerContent = {
                AppBarTitle(text = "Title")
            },
            endContent = {
                ClickableIcon(
                    icon = Icons.AutoMirrored.Filled.ArrowBack
                ) {}
            }
        )
    }
}