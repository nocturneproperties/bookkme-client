package me.bookk.feature.settings.presentation.accdelete

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.error.PresentationNotification.GlobalMessage
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.feature.settings.domain.api.DeleteAccount
import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.accdelete.DeleteAccountNavigationDestination.Back
import me.bookk.feature.settings.presentation.confirmation.ChatConfirmationRequest

class DeleteAccountViewModel(
    private val deleteAccount: DeleteAccount,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: DeleteAccountState = settingsStateFactory.createDeleteAccountState().setup()

    private val deletion = ChatConfirmationRequest(
        viewModel = this,
        key = DELETE_KEY,
        state = uiState.chatConfirmation,
        button = uiState.deleteButton,
        notifications = uiState.notifications,
        action = { deleteAccount() },
        onCompleted = { onAccountDeleted() },
        onError = { uiState.notifications.add(it.notification()) },
        isActionAllowed = { uiState.confirmation.isChecked },
    )

    private fun DeleteAccountState.setup() = apply {
        appBar.title = SettingsRes.strings.settings_delete_account_title.desc()
        appBar.size = TopBarSize.LARGE
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(Back) }
        confirmationMessage = SettingsRes.strings.settings_delete_account_confirmation.desc()
        confirmation.text = SettingsRes.strings.settings_delete_account_confirmation_switch.desc()
        confirmation.isChecked = false
        confirmation.onCheckedChange = weakVMClosure { vm, isChecked: Boolean ->
            vm.onAgreementChanged(isChecked)
        }
        deleteButton.text = SettingsRes.strings.settings_delete_account_confirmation_button.desc()
        deleteButton.isEnabled = false
        deleteButton.onClick = weakVMClosure { it.deletion.send() }
        chatConfirmation.onWriteAccessAnswered = weakVMClosure { vm, isGranted: Boolean ->
            vm.deletion.onWriteAccessAnswered(isGranted)
        }
    }

    private fun onAgreementChanged(isChecked: Boolean) {
        uiState.confirmation.isChecked = isChecked
        uiState.deleteButton.isEnabled = deletion.canSend()
    }

    private fun onAccountDeleted() {
        uiState.notifications.add(PresentationNotification.SuccessHaptic)
        uiState.notifications.add(
            GlobalMessage(SettingsRes.strings.settings_delete_account_success_message.desc()),
        )
    }

    private companion object {
        const val DELETE_KEY = "deleteAccount"
    }
}
