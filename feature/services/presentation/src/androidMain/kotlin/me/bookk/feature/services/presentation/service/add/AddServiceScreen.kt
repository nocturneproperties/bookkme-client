package me.bookk.feature.services.presentation.service.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import library.picker.PickerScreenArgs
import me.bookk.core.domain.entity.KeyValueData
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.CheckBox
import me.bookk.designsystem.components.CollapsingAppBarScaffold
import me.bookk.designsystem.components.PickerField
import me.bookk.designsystem.components.TextField
import me.bookk.feature.services.presentation.LocalNavigation

@Composable
internal fun AddServiceScreen(state: AddServiceState) {
    CollapsingAppBarScaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            AppTopBar(
                state = state.appBar,
                behavior = it
            )
        },
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .padding(all = 16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val navigation = LocalNavigation.current
                val title = state.group.pickerTitle.localized()
                val context = LocalContext.current
                PickerField(
                    state = state.group,
                    navController = navigation.controller(),
                    navigateToScreenPicker = {
                        navigation.navigateToPicker(
                            PickerScreenArgs(
                                id = state.id,
                                title = title,
                                options = it.map { presentation ->
                                    PickerScreenArgs.PickerData(
                                        iconUrl = presentation.displayIconUrl,
                                        data = KeyValueData(
                                            key = presentation.pickerItemId,
                                            value = presentation.displayName.toString(context)
                                        )
                                    )
                                },
                                choice = PickerScreenArgs.Choice.SINGLE
                            )
                        )
                    }
                )
                TextField(state.name)
                TextField(state.duration)
                TextField(state.price)
                CheckBox(state.enabled)
            }
        },
        bottomBar = {
            ActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                state = state.create
            )
        }
    )
}