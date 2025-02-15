package me.bookk.designsystem.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import me.bookk.designsystem.theme.color.LocalColors

@Composable
fun ClickableIcon(
    modifier: Modifier = Modifier,
    icon: Int,
    tint: Color = LocalColors.current.primaryText,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            modifier = modifier,
            painter = painterResource(icon),
            contentDescription = null,
            tint = tint
        )
    }
}

@Composable
fun ClickableIcon(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    tint: Color = LocalColors.current.primaryText,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            modifier = modifier,
            imageVector = icon,
            contentDescription = null,
            tint = tint
        )
    }
}