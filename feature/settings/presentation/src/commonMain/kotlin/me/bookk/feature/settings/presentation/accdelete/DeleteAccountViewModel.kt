package me.bookk.feature.settings.presentation.accdelete

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.settings.presentation.SettingsStateFactory

class DeleteAccountViewModel(
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = settingsStateFactory.createDeleteAccountState(createInitData())

    fun onSwitchStateChanged(isChecked: Boolean) {
        uiState.confirmationSwitch.isChecked = isChecked
        uiState.deleteButton.isEnabled = isChecked
    }

    fun onDeleteClick() {

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