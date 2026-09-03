package me.bookk.feature.clients.presentation.edit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.StateTextButton
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.components.stateButtonColors
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.secondary

@Composable
internal fun EditClientScreen(state: EditClientState) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            AppTopBar(state = state.appBar)
        },
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AnimatedVisibility(visible = state.isAttachedInfoVisible) {
                    AppCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = state.attachedInfoText.localized(),
                            style = MaterialTheme.typography.bodyMedium.secondary()
                        )
                    }
                }
                TextField(state.name, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next))
                TextField(
                    state.lastName,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                TextField(
                    state.phone,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                TextField(
                    state.email,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                TextField(
                    state.description,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                    minLines = 3
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    state = state.submit
                )
                StateTextButton(
                    modifier = Modifier.fillMaxWidth(),
                    state = state.deleteButton,
                    colors = ButtonDefaults.stateButtonColors(contentColor = LocalColors.current.error)
                )
            }
        }
    )
}
