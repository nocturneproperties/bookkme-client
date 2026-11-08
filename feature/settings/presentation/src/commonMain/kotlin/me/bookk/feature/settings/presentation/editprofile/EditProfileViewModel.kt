package me.bookk.feature.settings.presentation.editprofile

import dev.icerock.moko.resources.desc.desc
import library.validation.api.ValidateName
import library.validation.api.ValidateName.Result.Invalid.Length.isValid
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.clearError
import me.bookk.designsystem.uistate.showError
import me.bookk.designsystem.uistate.validate
import me.bookk.feature.settings.domain.api.EditProfile
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.entity.SettingsProfile
import me.bookk.feature.settings.presentation.SettingsStateFactory

class EditProfileViewModel(
    private val getSettings: GetSettings,
    private val editProfile: EditProfile,
    private val validateName: ValidateName,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    private enum class Field {
        NAME,
        LAST,
    }

    val uiState: EditProfileState = settingsStateFactory.createEditProfileState(
        createInitData(),
    ).apply {
        email.readOnly = true
        showEmailHint()
        changeEmailButton.text = SettingsRes.strings.settings_edit_profile_change_email.desc()
        changeEmailButton.onClick = weakVMClosure {
            it.uiState.navigation.push(EditProfileNavigationDestination.ChangeEmail)
        }
    }

    private val fieldInitialState = mutableMapOf<Field, String>()

    override fun onViewPresented() {
        loadCurrentUserProfile()
    }

    private fun loadCurrentUserProfile() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getSettings() },
            onComplete = { renderProfile(it.profile) },
            onError = { uiState.notification.add(errorMapper.mapToNotification(it)) },
        )
    }

    private fun renderProfile(profile: SettingsProfile) {
        uiState.email.text = profile.email
        if (fieldInitialState.isNotEmpty()) return
        fieldInitialState[Field.NAME] = profile.firstName
        fieldInitialState[Field.LAST] = profile.lastName
        onFirstNameTextChanged(profile.firstName)
        onLastNameTextChanged(profile.lastName)
    }

    fun onFirstNameTextChanged(text: String) {
        if (fieldInitialState.isEmpty()) return
        uiState.name.text = text
        uiState.name.clearError()
        validateButton()
    }

    fun onLastNameTextChanged(text: String) {
        if (fieldInitialState.isEmpty()) return
        uiState.lastName.text = text
        uiState.lastName.clearError()
        validateButton()
    }

    private fun validateForm(): Boolean {
        return listOf(
            uiState.name.validate(
                SettingsRes.strings.settings_edit_profile_first_name_error.desc()
                    .takeIf { !validateName(uiState.name.text).isValid },
            ),
            uiState.lastName.validate(
                SettingsRes.strings.settings_edit_profile_last_name_error.desc()
                    .takeIf { !validateName(uiState.lastName.text).isValid },
            ),
        ).all { it }
    }

    fun onConfirmButtonClick() {
        if (!validateForm()) {
            validateButton()
            return
        }
        launch(
            launchIn = DispatcherProvider.io,
            onStart = {
                uiState.showEmailHint()
                uiState.confirmButton.isLoading = true
                uiState.confirmButton.isEnabled = false
            },
            call = {
                editProfile(
                    firstName = uiState.name.text,
                    lastName = uiState.lastName.text,
                )
            },
            onComplete = {
                uiState.notification.add(PresentationNotification.SuccessHaptic)
                fieldInitialState[Field.NAME] = uiState.name.text
                fieldInitialState[Field.LAST] = uiState.lastName.text
                uiState.navigation.push(EditProfileNavigationDestination.Back)
            },
            onError = {
                when (it) {
                    is EditProfile.Error.ContactAlreadyTaken -> {
                        uiState.email.showError(
                            SettingsRes.strings.settings_edit_profile_email_taken_error.desc(),
                        )
                    }

                    is EditProfile.Error.EmailChangeRequiresVerification -> {
                        uiState.email.showError(
                            SettingsRes.strings
                                .settings_edit_profile_email_verification_required_error.desc(),
                        )
                    }

                    else -> {
                        uiState.notification.add(it.notification())
                    }
                }
            },
            onTerminate = {
                uiState.confirmButton.isLoading = false
                validateButton()
            },
        )
    }

    private fun EditProfileState.showEmailHint() {
        email.clearError()
        email.supportingTextRes =
            SettingsRes.strings.settings_edit_profile_email_read_only_hint.desc()
    }

    private fun validateButton() {
        val isChanged = (fieldInitialState[Field.NAME] != uiState.name.text) ||
            (fieldInitialState[Field.LAST] != uiState.lastName.text)
        uiState.confirmButton.isEnabled = isChanged &&
            uiState.name.isValid &&
            uiState.lastName.isValid
    }

    companion object {
        fun createInitData() = EditProfileState.InitData(
            title = SettingsRes.strings.settings_edit_profile_title.desc(),
            nameHint = SettingsRes.strings.settings_edit_profile_first_name.desc(),
            lastNameHint = SettingsRes.strings.settings_edit_profile_last_name.desc(),
            emailHint = SettingsRes.strings.settings_edit_profile_email.desc(),
            confirmButtonText = DesignSystem.strings.action_save.desc(),
        )
    }
}
