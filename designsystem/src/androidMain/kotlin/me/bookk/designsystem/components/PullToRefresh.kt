package me.bookk.designsystem.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.RefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefresh(
    state: RefreshState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit
) {
    val refreshState = rememberPullToRefreshState()
    PullToRefreshBox(
        modifier = modifier,
        contentAlignment = contentAlignment,
        isRefreshing = state.isRefreshing,
        onRefresh = onRefresh,
        state = refreshState,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = state.isRefreshing,
                state = refreshState,
                containerColor = LocalColors.current.background,
                color = LocalColors.current.primaryText
            )
        },
        content = content
    )
}