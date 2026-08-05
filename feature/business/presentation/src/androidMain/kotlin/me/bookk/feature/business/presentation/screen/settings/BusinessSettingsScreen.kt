package me.bookk.feature.business.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.FlatTextField
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.PickerField
import me.bookk.designsystem.components.StateTextButton
import me.bookk.designsystem.modifier.bottomShadow
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.business.presentation.screen.settings.state.BusinessSettingsState

@Composable
internal fun BusinessSettingsScreen(state: BusinessSettingsState) {
    Scaffold(
        topBar = {
            AppTopBar(
                state = state.appBar,
                actions = {
                    StateTextButton(
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BusinessProfileCard(state)
                    ScheduleSection(state)
                    BusinessLocationCard(state)
                    Column {
                        Header(BusinessRes.strings.business_settings_currency_title.desc().localized())
                        PickerField(
                            state = state.currency,
                            onItemPicked = LocalBusinessSettingsEventListener.current.onCurrencySelected
                        )
                    }
                    BusinessSocialsCard(state)
                }
            }
        }
    )
}

@Composable
private fun BusinessProfileCard(state: BusinessSettingsState) {
    val listener = LocalBusinessSettingsEventListener.current
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PhotoBadge(onClick = listener.onAddPhotoClick, contentDescription = state.photo.text)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = BusinessRes.strings.business_settings_name_title.desc().localized(),
                        style = MaterialTheme.typography.bodySmall,
                        color = LocalColors.current.secondaryText
                    )
                    FlatTextField(
                        state = state.name,
                        onValueChange = listener.onNameChanged,
                        textStyle = MaterialTheme.typography.titleMedium,
                        singleLine = true
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = LocalColors.current.divider
            )
            Column {
                Text(
                    text = BusinessRes.strings.business_settings_description_title.desc().localized(),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalColors.current.secondaryText
                )
                FlatTextField(
                    state = state.description,
                    onValueChange = listener.onDescriptionChanged,
                    textStyle = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun PhotoBadge(onClick: () -> Unit, contentDescription: StringDesc) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(LocalColors.current.buttonPrimary)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.AddAPhoto,
            contentDescription = contentDescription.localized(),
            tint = LocalColors.current.onAction,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun BusinessLocationCard(state: BusinessSettingsState) {
    val listener = LocalBusinessSettingsEventListener.current
    Column {
        Header(BusinessRes.strings.business_settings_location_title.desc().localized())
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(LocalColors.current.background)
                        .clickable(onClick = listener.onTestLocationClick)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = null,
                        tint = LocalColors.current.secondaryText,
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center)
                    )
                    Text(
                        text = state.testLocation.text.localized(),
                        style = MaterialTheme.typography.labelMedium,
                        color = LocalColors.current.primaryText,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(LocalColors.current.elevated)
                            .clickable(onClick = listener.onTestLocationClick)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FlatTextField(
                        state = state.address,
                        onValueChange = listener.onAddressChanged,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        singleLine = true
                    )
                    StateTextButton(
                        state = state.pickLocation,
                        onClick = listener.onPickLocationClick
                    )
                }
            }
        }
    }
}

@Composable
private fun BusinessSocialsCard(state: BusinessSettingsState) {
    val listener = LocalBusinessSettingsEventListener.current
    Column {
        Header(BusinessRes.strings.business_settings_socials_title.desc().localized())
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                FlatTextField(
                    state = state.phone,
                    onValueChange = listener.onPhoneChanged,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = LocalColors.current.divider
                )
                FlatTextField(
                    state = state.telegram,
                    onValueChange = listener.onTelegramChanged
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = LocalColors.current.divider
                )
                FlatTextField(
                    state = state.instagram,
                    onValueChange = listener.onInstagramChanged
                )
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = LocalColors.current.divider
                )
                FlatTextField(
                    state = state.viber,
                    onValueChange = listener.onViberChanged,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text
                    )
                )
            }
        }
    }
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
