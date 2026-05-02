package me.bookk.designsystem.components

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.html
import me.bookk.designsystem.painter
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.TopBarSize


@Composable
fun AppTopBar(
    state: AppBarState,
    modifier: Modifier = Modifier,
    colors: TopAppBarColors = topBarDefaultColors(),
    actions: @Composable RowScope.() -> Unit = {
        state.actions.items.forEach {
            IconButton(onClick = it.onClick) {
                Icon(
                    painter = it.icon.painter(),
                    contentDescription = it.contentDescription?.localized(),
                    tint = colors.actionIconContentColor
                )
            }
        }
    },
    onNavigationIconClick: (() -> Unit)? = state.onBackClick
) {
    when (state.size) {
        TopBarSize.SMALL -> {
            CenterAlignedTopAppBar(
                modifier = modifier,
                colors = colors,
                actions = actions,
                title = {
                    AppBarTitle(state.title.localized(), state.size, colors.titleContentColor)
                },
                navigationIcon = {
                    onNavigationIconClick?.let {
                        ClickableIcon(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = it,
                            tint = colors.navigationIconContentColor
                        )
                    }
                }
            )
        }

        TopBarSize.LARGE -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MediumTopAppBar(
                    modifier = modifier,
                    colors = colors,
                    actions = actions,
                    title = {
                        AppBarTitle(
                            state.title.localized(),
                            state.size,
                            colors.titleContentColor
                        )
                    },
                    navigationIcon = {
                        onNavigationIconClick?.let {
                            ClickableIcon(
                                icon = Icons.AutoMirrored.Filled.ArrowBack,
                                onClick = it,
                                tint = colors.navigationIconContentColor
                            )
                        }
                    }
                )
                state.subtitle?.let {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = it.html(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun AppBarTitle(text: String, size: TopBarSize, color: Color) {
    val style = when (size) {
        TopBarSize.SMALL -> MaterialTheme.typography.headlineSmall
        TopBarSize.LARGE -> MaterialTheme.typography.headlineLarge
    }
    Text(
        text = text,
        style = style,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        color = color
    )
}

@Composable
fun topBarDefaultColors(
    containerColor: Color = Color.Transparent,
    scrolledContainerColor: Color = Color.Unspecified,
    navigationIconContentColor: Color = Color.Unspecified,
    titleContentColor: Color = Color.Unspecified,
    actionIconContentColor: Color = Color.Unspecified,
    subtitleContentColor: Color = Color.Unspecified,
) = TopAppBarDefaults.topAppBarColors(
    containerColor,
    scrolledContainerColor,
    navigationIconContentColor,
    titleContentColor,
    actionIconContentColor,
    subtitleContentColor
)

@Preview(showBackground = true, backgroundColor = BLACK.toLong())
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        Column {
            AppTopBar(
                state = AndroidAppBarState(
                    title = "Title".desc(),
                    size = TopBarSize.SMALL,
                ),
                onNavigationIconClick = {}
            )
            AppTopBar(
                state = AndroidAppBarState(
                    title = "Title".desc(),
                    subtitle = "Subtitle example".desc(),
                    size = TopBarSize.LARGE,
                ),
                onNavigationIconClick = {}
            )
            AppTopBar(
                state = AndroidAppBarState(
                    title = "Title".desc(),
                    subtitle = "Subtitle example".desc(),
                    size = TopBarSize.LARGE,
                ),
                onNavigationIconClick = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = WHITE.toLong())
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        Column {
            AppTopBar(
                state = AndroidAppBarState(
                    title = "Title".desc(),
                    subtitle = null,
                    size = TopBarSize.SMALL,
                ),
                onNavigationIconClick = {}
            )
            AppTopBar(
                state = AndroidAppBarState(
                    title = "Title".desc(),
                    subtitle = "Subtitle example".desc(),
                    size = TopBarSize.LARGE,
                ),
                onNavigationIconClick = {}
            )
        }
    }
}