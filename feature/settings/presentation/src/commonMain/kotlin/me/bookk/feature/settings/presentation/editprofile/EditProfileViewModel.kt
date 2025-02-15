package me.bookk.feature.settings.presentation.editprofile

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.feature.authorization.domain.api.ValidateEmail
import me.bookk.feature.authorization.domain.api.ValidateEmail.Result.Invalid.Format.isValid
import me.bookk.feature.authorization.domain.api.ValidateName
import me.bookk.feature.authorization.domain.api.ValidateName.Result.Invalid.Length.isValid
import me.bookk.feature.settings.domain.api.EditProfile
import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.editprofile.state.EditProfileState

class EditProfileViewModel(
    private val editProfile: EditProfile,
    private val validateName: ValidateName,
    private val validateEmail: ValidateEmail,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = settingsStateFactory.createEditProfileState(createInitData())

    fun onFirstNameTextChanged(text: String) {
        val validationResult = validateName.invoke(text)
        uiState.name.text = text
        uiState.name.isValid = validationResult.isValid
        uiState.name.isError = !validationResult.isValid
        uiState.name.errorTextRes = when (validationResult) {
            ValidateName.Result.Invalid.Length -> SettingsRes.strings.settings_edit_profile_first_name_error.desc()
            ValidateName.Result.Valid -> null
        }
        validateButton()
    }

    fun onLastNameTextChanged(text: String) {
        val validationResult = validateName.invoke(text)
        uiState.lastName.text = text
        uiState.lastName.isValid = validationResult.isValid
        uiState.lastName.isError = !validationResult.isValid
        uiState.lastName.errorTextRes = when (validationResult) {
            ValidateName.Result.Invalid.Length -> SettingsRes.strings.settings_edit_profile_last_name_error.desc()
            ValidateName.Result.Valid -> null
        }
        validateButton()
    }

    fun onEmailTextChanged(text: String) {
        val validationResult = validateEmail.invoke(text)
        uiState.email.text = text
        uiState.email.isValid = validationResult.isValid
        uiState.email.isError = !validationResult.isValid
        uiState.email.errorTextRes = when (validationResult) {
            ValidateEmail.Result.Invalid.Format -> SettingsRes.strings.settings_edit_profile_email_error.desc()
            ValidateEmail.Result.Valid -> null
        }
        validateButton()
    }

    fun onConfirmButtonClick() {

    }

    private fun validateButton() {
        uiState.confirmButton.isEnabled = uiState.name.isValid &&
                uiState.lastName.isValid &&
                uiState.email.isValid
    }

    companion object {
        fun createInitData() = EditProfileState.InitData(
            title = SettingsRes.strings.settings_edit_profile_title.desc(),
            nameHint = SettingsRes.strings.settings_edit_profile_first_name.desc(),
            lastNameHint = SettingsRes.strings.settings_edit_profile_last_name.desc(),
            emailHint = SettingsRes.strings.settings_edit_profile_email.desc(),
            confirmButtonText = DesignSystem.strings.action_confirm.desc()
        )
    }
}