package me.bookk.feature.settings.presentation.editprofile

import dev.icerock.moko.resources.desc.StringDesc
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
import me.bookk.designsystem.uistate.InputType
import me.bookk.designsystem.uistate.clearError
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

    val uiState: EditProfileState = settingsStateFactory.createEditProfileState().setup()

    private val fieldInitialState = mutableMapOf<Field, String>()

    private fun EditProfileState.setup() = apply {
        appBar.title = SettingsRes.strings.settings_edit_profile_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(EditProfileNavigationDestination.Back)
        }
        setupNameFields()
        setupEmail()
        confirmButton.text = DesignSystem.strings.action_save.desc()
        confirmButton.isEnabled = false
        confirmButton.onClick = weakVMClosure { it.onConfirmButtonClick() }
    }

    private fun EditProfileState.setupNameFields() {
        name.label = SettingsRes.strings.settings_edit_profile_first_name.desc()
        name.onTextChanged = weakVMClosure { vm, text -> vm.onFirstNameTextChanged(text) }
        lastName.label = SettingsRes.strings.settings_edit_profile_last_name.desc()
        lastName.onTextChanged = weakVMClosure { vm, text -> vm.onLastNameTextChanged(text) }
    }

    private fun EditProfileState.setupEmail() {
        email.label = SettingsRes.strings.settings_profile_email.desc()
        email.inputType = InputType.EMAIL
        email.readOnly = true
        email.supportingTextRes = SettingsRes.strings.settings_edit_profile_email_hint.desc()
        changeEmail.text = SettingsRes.strings.settings_edit_profile_change_email.desc()
        changeEmail.onClick = weakVMClosure {
            it.uiState.navigation.push(EditProfileNavigationDestination.ChangeEmail)
        }
    }

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
        val firstName = uiState.name.text
        val lastName = uiState.lastName.text
        launch(
            launchIn = DispatcherProvider.io,
            onStart = {
                uiState.confirmButton.isLoading = true
                uiState.confirmButton.isEnabled = false
            },
            call = { editProfile(firstName = firstName, lastName = lastName) },
            onComplete = {
                uiState.notification.add(PresentationNotification.SuccessHaptic)
                fieldInitialState[Field.NAME] = firstName
                fieldInitialState[Field.LAST] = lastName
                uiState.navigation.push(EditProfileNavigationDestination.Back)
            },
            onError = ::onSaveError,
            onTerminate = {
                uiState.confirmButton.isLoading = false
                validateButton()
            },
        )
    }

    private fun onSaveError(error: Throwable) {
        when (error) {
            is EditProfile.Error.ContactAlreadyTaken -> {
                showErrorMessage(
                    SettingsRes.strings.settings_edit_profile_email_taken_error.desc(),
                )
            }

            is EditProfile.Error.EmailChangeRequiresVerification -> {
                showErrorMessage(
                    SettingsRes.strings
                        .settings_edit_profile_email_verification_required_error.desc(),
                )
            }

            else -> {
                uiState.notification.add(error.notification())
            }
        }
    }

    private fun showErrorMessage(text: StringDesc) {
        uiState.notification.add(
            PresentationNotification.GlobalMessage(
                text = text,
                state = PresentationNotification.GlobalMessage.State.ERROR,
            ),
        )
    }

    private fun validateButton() {
        val isChanged = (fieldInitialState[Field.NAME] != uiState.name.text) ||
            (fieldInitialState[Field.LAST] != uiState.lastName.text)
        uiState.confirmButton.isEnabled = isChanged &&
            uiState.name.isValid &&
            uiState.lastName.isValid
    }
}
