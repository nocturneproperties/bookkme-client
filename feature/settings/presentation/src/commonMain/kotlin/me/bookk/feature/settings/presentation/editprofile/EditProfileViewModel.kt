package me.bookk.feature.settings.presentation.editprofile

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.authorization.domain.api.ValidateEmail
import me.bookk.feature.authorization.domain.api.ValidateEmail.Result.Invalid.Format.isValid
import me.bookk.feature.authorization.domain.api.ValidateName
import me.bookk.feature.authorization.domain.api.ValidateName.Result.Invalid.Length.isValid
import me.bookk.feature.settings.domain.api.EditProfile
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.presentation.SettingsStateFactory

class EditProfileViewModel(
    private val getSettings: GetSettings,
    private val editProfile: EditProfile,
    private val validateName: ValidateName,
    private val validateEmail: ValidateEmail,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    private enum class Field {
        NAME,
        LAST,
        EMAIL
    }

    val uiState = settingsStateFactory.createEditProfileState(createInitData())

    private val fieldInitialState = mutableMapOf<Field, String>()

    override fun onViewPresented() {
        loadCurrentUserProfile()
    }

    private fun loadCurrentUserProfile() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getSettings() },
            onComplete = {
                fieldInitialState[Field.NAME] = it.profile.firstName
                fieldInitialState[Field.LAST] = it.profile.lastName
                fieldInitialState[Field.EMAIL] = it.profile.email
                onFirstNameTextChanged(it.profile.firstName)
                onLastNameTextChanged(it.profile.lastName)
                onEmailTextChanged(it.profile.email)
            },
            onError = { uiState.notification.add(errorMapper.mapToNotification(it)) }
        )
    }

    fun onFirstNameTextChanged(text: String) {
        if (fieldInitialState.isEmpty()) return
        val validationResult = validateName.invoke(text)
        uiState.name.text = text
        uiState.name.isValid = validationResult.isValid
        uiState.name.validationState =
            if (!validationResult.isValid) ValidationState.ERROR else ValidationState.DEFAULT
        uiState.name.supportingTextRes = when (validationResult) {
            ValidateName.Result.Invalid.Length -> SettingsRes.strings.settings_edit_profile_first_name_error.desc()
            ValidateName.Result.Valid -> null
        }
        validateButton()
    }

    fun onLastNameTextChanged(text: String) {
        if (fieldInitialState.isEmpty()) return
        val validationResult = validateName.invoke(text)
        uiState.lastName.text = text
        uiState.lastName.isValid = validationResult.isValid
        uiState.lastName.validationState =
            if (!validationResult.isValid) ValidationState.ERROR else ValidationState.DEFAULT
        uiState.lastName.supportingTextRes = when (validationResult) {
            ValidateName.Result.Invalid.Length -> SettingsRes.strings.settings_edit_profile_last_name_error.desc()
            ValidateName.Result.Valid -> null
        }
        validateButton()
    }

    fun onEmailTextChanged(text: String) {
        if (fieldInitialState.isEmpty()) return
        val validationResult = validateEmail.invoke(text)
        uiState.email.text = text
        uiState.email.isValid = validationResult.isValid
        uiState.email.validationState =
            if (!validationResult.isValid) ValidationState.ERROR else ValidationState.DEFAULT
        uiState.email.supportingTextRes = when (validationResult) {
            ValidateEmail.Result.Invalid.Format -> SettingsRes.strings.settings_edit_profile_email_error.desc()
            ValidateEmail.Result.Valid -> null
        }
        validateButton()
    }

    fun onConfirmButtonClick() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = {
                uiState.confirmButton.isLoading = true
                uiState.confirmButton.isEnabled = false
            },
            call = {
                editProfile(
                    firstName = uiState.name.text,
                    lastName = uiState.lastName.text,
                    email = uiState.email.text
                )
            },
            onComplete = {
                fieldInitialState[Field.NAME] = uiState.name.text
                fieldInitialState[Field.LAST] = uiState.lastName.text
                fieldInitialState[Field.EMAIL] = uiState.email.text
                uiState.navigation.push(EditProfileNavigationDestination.Back)
            },
            onError = {
                uiState.notification.add(errorMapper.mapToNotification(it))
            },
            onTerminate = {
                uiState.confirmButton.isLoading = false
                validateButton()
            }
        )
    }

    private fun validateButton() {
        val isChanged = (fieldInitialState[Field.NAME] != uiState.name.text) ||
                (fieldInitialState[Field.LAST] != uiState.lastName.text) ||
                (fieldInitialState[Field.EMAIL] != uiState.email.text)
        uiState.confirmButton.isEnabled = isChanged &&
                uiState.name.isValid &&
                uiState.lastName.isValid &&
                uiState.email.isValid

    }

    companion object {
        fun createInitData() = EditProfileState.InitData(
            title = SettingsRes.strings.settings_edit_profile_title.desc(),
            nameHint = SettingsRes.strings.settings_edit_profile_first_name.desc(),
            lastNameHint = SettingsRes.strings.settings_edit_profile_last_name.desc(),
            emailHint = SettingsRes.strings.settings_edit_profile_email.desc(),
            confirmButtonText = DesignSystem.strings.action_save.desc()
        )
    }
}