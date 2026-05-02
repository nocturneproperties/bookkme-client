package me.bookk.feature.business.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.PickerField
import me.bookk.designsystem.components.TextButton
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.modifier.bottomShadow
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.business.presentation.settings.state.BusinessSettingsState

@Composable
internal fun BusinessSettingsScreen(state: BusinessSettingsState) {
    Scaffold(
        topBar = {
            AppTopBar(
                state = state.appBar,
                actions = {
                    TextButton(
                        state = state.save,
                        onClick = LocalBusinessSettingsEventListener.current.onSaveClick
                    )
                },
                onNavigationIconClick = LocalBusinessSettingsEventListener.current.onBackClick
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .bottomShadow(32.dp, LocalColors.current.background)
            ) {
                Column(
                    modifier = Modifier
                        .padding(it)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Column {
                        Header(BusinessRes.strings.business_settings_name_title.desc())
                        TextField(
                            state = state.name,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            onValueChange = LocalBusinessSettingsEventListener.current.onNameChanged
                        )
                    }
                    Column {
                        Header(BusinessRes.strings.business_settings_description_title.desc())
                        TextField(
                            state = state.description,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            onValueChange = LocalBusinessSettingsEventListener.current.onDescriptionChanged
                        )
                    }
                    Column {
                        Header(BusinessRes.strings.business_settings_location_title.desc())
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                modifier = Modifier.weight(1f),
                                state = state.location,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                onValueChange = {}
                            )
                            TextButton(
                                state = state.pickLocation,
                                onClick = LocalBusinessSettingsEventListener.current.onPickLocationClick
                            )
                        }
                        TextButton(
                            state = state.testLocation,
                            onClick = LocalBusinessSettingsEventListener.current.onTestLocationClick
                        )
                    }
                    Column {
                        Header(BusinessRes.strings.business_settings_address_title.desc())
                        TextField(
                            state = state.address,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            onValueChange = LocalBusinessSettingsEventListener.current.onAddressChanged
                        )
                    }
                    Column {
                        Header(BusinessRes.strings.business_settings_currency_title.desc())
                        PickerField(
                            state = state.currency,
                            onItemPicked = LocalBusinessSettingsEventListener.current.onCurrencySelected
                        )
                    }
                    Column {
                        Header(BusinessRes.strings.business_settings_socials_title.desc())
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextField(
                                state = state.phone,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                onValueChange = LocalBusinessSettingsEventListener.current.onPhoneChanged
                            )
                            TextField(
                                state = state.telegram,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                onValueChange = LocalBusinessSettingsEventListener.current.onTelegramChanged
                            )
                            TextField(
                                state = state.instagram,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                onValueChange = LocalBusinessSettingsEventListener.current.onInstagramChanged
                            )
                            TextField(
                                state = state.viber,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    keyboardType = KeyboardType.Text
                                ),
                                onValueChange = LocalBusinessSettingsEventListener.current.onViberChanged
                            )
                        }
                    }
                }
            }
        }
    )
}

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        BusinessSettingsScreen(
            state = AndroidBusinessSettingsState(BusinessSettingsViewModel.createInitData())
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        BusinessSettingsScreen(
            state = AndroidBusinessSettingsState(BusinessSettingsViewModel.createInitData())
        )
    }
}