package me.bookk.feature.settings.presentation.contactus

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.feature.settings.presentation.SettingsStateFactory

class ContactUsViewModel(
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = settingsStateFactory.createContactUsState(createInitData())

    fun onSubmitClick() {

    }

    fun onContactTextChanged(text: String) {
        uiState.contactField.text = text
        uiState.submitButton.isEnabled = text.isNotBlank()
    }

    fun onIncludeLogsStateChanged(include: Boolean) {
        uiState.includeLogsSwitch.isChecked = include
    }

    companion object {
        fun createInitData() = ContactUsState.InitData(
            title = SettingsRes.strings.settings_contact_us_title.desc(),
            contactHint = SettingsRes.strings.settings_contact_us_contact_hint.desc(),
            usageLogsText = SettingsRes.strings.settings_contact_us_include_logs.desc(),
            includeLogsExplanation = SettingsRes.strings.settings_contact_us_include_logs_hint.desc(),
            submitButtonText = DesignSystem.strings.action_send.desc()
        )
    }
}