package me.bookk.feature.settings.presentation.accdelete

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification.GlobalMessage
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.feature.settings.domain.api.DeleteAccount
import me.bookk.feature.settings.presentation.SettingsStateFactory

class DeleteAccountViewModel(
    private val deleteAccount: DeleteAccount,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = settingsStateFactory.createDeleteAccountState(createInitData())

    init {
        uiState.appBar.size = TopBarSize.LARGE
    }

    fun onSwitchStateChanged(isChecked: Boolean) {
        uiState.confirmation.isChecked = isChecked
        uiState.deleteButton.isEnabled = isChecked
    }

    fun onDeleteClick() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.deleteButton.isLoading = true },
            call = { deleteAccount() },
            onComplete = {
                uiState.notifications.add(GlobalMessage(SettingsRes.strings.settings_delete_account_success_message.desc()))
            },
            onError = { uiState.notifications.add(errorMapper.mapToNotification(it)) },
            onTerminate = { uiState.deleteButton.isLoading = false }
        )
    }

    companion object {
        fun createInitData(): DeleteAccountState.InitData {
            return DeleteAccountState.InitData(
                title = SettingsRes.strings.settings_delete_account_title.desc(),
                confirmationMessage = SettingsRes.strings.settings_delete_account_confirmation.desc(),
                switchMessage = SettingsRes.strings.settings_delete_account_confirmation_switch.desc(),
                buttonMessage = SettingsRes.strings.settings_delete_account_confirmation_button.desc()
            )
        }
    }

}