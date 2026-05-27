@file:OptIn(ExperimentalMaterial3Api::class)

package library.picker

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import library.picker.PickOptionViewModel.Companion.initPreview
import library.picker.PickerScreenArgs.PickerData
import library.picker.state.AndroidPickOptionState
import library.picker.state.PickOptionItem
import library.picker.state.PickOptionState
import me.bookk.core.domain.entity.KeyValueData
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.CheckBoxSelector
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.modifier.bottomShadow
import me.bookk.designsystem.modifier.topShadow
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors

@Composable
fun PickOptionScreen(
    state: PickOptionState
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
                PickOptionItemView(modifier = Modifier.animateItem(), it)
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
private fun PickOptionItemView(modifier: Modifier, item: PickOptionItem) {
    CheckBoxSelector(
        modifier = modifier.fillMaxWidth(),
        state = item.checkBox
    )
}

@Preview(showBackground = true, backgroundColor = BLACK.toLong())
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        val factory = remember { AndroidPickOptionStateFactory() }
        PickOptionScreen(
            AndroidPickOptionState().initPreview(
                args = PickerScreenArgs(
                    id = "rand",
                    title = "Select country",
                    options = listOf(
                        PickerData(KeyValueData("Key", "Value1")),
                        PickerData(KeyValueData("Key", "Value2")),
                        PickerData(KeyValueData("Key", "Value3")),
                        PickerData(KeyValueData("Key", "Value4"))
                    ),
                    choice = PickerScreenArgs.Choice.SINGLE
                ), createItem = factory::createPickOptionItem
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = WHITE.toLong())
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        val factory = remember { AndroidPickOptionStateFactory() }
        PickOptionScreen(
            AndroidPickOptionState().initPreview(
                args = PickerScreenArgs(
                    id = "rand",
                    title = "Select country",
                    options = listOf(
                        PickerData(KeyValueData("Key", "Value1")),
                        PickerData(KeyValueData("Key", "Value2")),
                        PickerData(KeyValueData("Key", "Value3")),
                        PickerData(KeyValueData("Key", "Value4"))
                    ),
                    choice = PickerScreenArgs.Choice.SINGLE
                ), createItem = factory::createPickOptionItem
            )
        )
    }
}