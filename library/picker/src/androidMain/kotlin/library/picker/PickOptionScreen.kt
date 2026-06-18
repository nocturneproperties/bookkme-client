@file:OptIn(ExperimentalMaterial3Api::class)

package library.picker

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import library.picker.state.PickOptionItem
import library.picker.state.PickOptionState
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.CheckBoxSelector
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.modifier.bottomShadow
import me.bookk.designsystem.modifier.topShadow
import me.bookk.designsystem.theme.color.LocalColors

@Composable
fun PickOptionScreen(
    state: PickOptionState,
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            val backPress = LocalOnBackPressedDispatcherOwner.current
            Column {
                AppTopBar(
                    state = state.appBar,
                    onNavigationIconClick = {
                        backPress?.onBackPressedDispatcher?.onBackPressed()
                    }
                )
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    state = state.queryField
                )
            }
        },
        content = { paddings ->
            List(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
                    .topShadow(24.dp, LocalColors.current.background)
                    .bottomShadow(24.dp, LocalColors.current.background),
                contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
                state = state.filteredOptions
            ) {
                Column(Modifier.animateItem()) {
                    PickOptionItemView(item = it)
                    HorizontalDivider(color = LocalColors.current.divider)
                }
            }
        },
        bottomBar = {
            if (state.selectButton.isVisible) {
                ActionButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    state = state.selectButton
                )
            }
        })
}

@Composable
private fun PickOptionItemView(modifier: Modifier = Modifier, item: PickOptionItem) {
    CheckBoxSelector(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        state = item.checkBox
    )
}