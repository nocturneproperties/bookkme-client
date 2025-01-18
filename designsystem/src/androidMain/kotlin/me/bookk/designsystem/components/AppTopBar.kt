package me.bookk.designsystem.components

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import dev.icerock.moko.resources.desc.desc
import me.bookk.core.presentation.string
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.AppBarStateImpl

enum class TopBarSize {
    SMALL,
    MEDIUM,
    LARGE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    state: AppBarState,
    size: TopBarSize,
    onNavigationIconClick: (() -> Unit)? = null
) {
    when (size) {
        TopBarSize.SMALL -> {
            TopAppBar(
                modifier = modifier,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LocalColors.current.Background
                ),
                title = {
                    AppBarTitle(state.title.string(), size)
                },
                navigationIcon = {
                    onNavigationIconClick?.let {
                        ClickableIcon(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = it
                        )
                    }
                }
            )
        }

        TopBarSize.MEDIUM -> {
            MediumTopAppBar(
                modifier = modifier,
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = LocalColors.current.Background
                ),
                title = {
                    AppBarTitle(state.title.string(), size)
                },
                navigationIcon = {
                    onNavigationIconClick?.let {
                        ClickableIcon(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = it
                        )
                    }
                }
            )
        }

        TopBarSize.LARGE -> {
            LargeTopAppBar(
                modifier = modifier,
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = LocalColors.current.Background
                ),
                title = {
                    AppBarTitle(state.title.string(), size)
                },
                navigationIcon = {
                    onNavigationIconClick?.let {
                        ClickableIcon(
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = it
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun AppBarTitle(text: String, size: TopBarSize) {
    val style = when(size) {
        TopBarSize.SMALL -> MaterialTheme.typography.headlineSmall
        TopBarSize.MEDIUM -> MaterialTheme.typography.headlineLarge
        TopBarSize.LARGE -> MaterialTheme.typography.headlineLarge
    }
    Text(
        text = text,
        style = style,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview(showBackground = true, backgroundColor = BLACK.toLong())
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        Column {
            AppTopBar(
                state = AppBarStateImpl(
                    title = "Title".desc(),
                    subtitle = null
                ),
                size = TopBarSize.SMALL,
                onNavigationIconClick = {}
            )
            AppTopBar(
                state = AppBarStateImpl(
                    title = "Title".desc(),
                    subtitle = null
                ),
                size = TopBarSize.MEDIUM,
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
                state = AppBarStateImpl(
                    title = "Title".desc(),
                    subtitle = null
                ),
                size = TopBarSize.SMALL,
                onNavigationIconClick = {}
            )
            AppTopBar(
                state = AppBarStateImpl(
                    title = "Title".desc(),
                    subtitle = null
                ),
                size = TopBarSize.MEDIUM,
                onNavigationIconClick = {}
            )
        }
    }
}