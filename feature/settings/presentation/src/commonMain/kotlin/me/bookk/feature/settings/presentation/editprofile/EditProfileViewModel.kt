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
import me.bookk.designsystem.uistate.startLoading
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

    val uiState: EditProfileState = settingsStateFactory.createEditProfileState().setup()

    private var savedName: String? = null

    private fun EditProfileState.setup() = apply {
        appBar.title = SettingsRes.strings.settings_edit_profile_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(EditProfileNavigationDestination.Back)
        }
        name.label = SettingsRes.strings.settings_edit_profile_full_name.desc()
        name.onTextChanged = weakVMClosure { vm, text -> vm.onNameTextChanged(text) }
        setupEmail()
        confirmButton.text = DesignSystem.strings.action_save.desc()
        confirmButton.isEnabled = false
        confirmButton.onClick = weakVMClosure { it.onConfirmButtonClick() }
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
        if (savedName != null) return
        val name = FullName.join(profile.firstName, profile.lastName)
        savedName = name
        onNameTextChanged(name)
    }

    fun onNameTextChanged(text: String) {
        if (savedName == null) return
        uiState.name.text = text
        uiState.name.clearError()
        updateConfirmButton()
    }

    fun onConfirmButtonClick() {
        val name = FullName.parse(uiState.name.text)
        if (!validate(name)) {
            updateConfirmButton()
            return
        }
        val text = uiState.name.text
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.confirmButton.startLoading() },
            call = { editProfile(firstName = name.firstName, lastName = name.lastName) },
            onComplete = {
                uiState.notification.add(PresentationNotification.SuccessHaptic)
                savedName = text
                uiState.navigation.push(EditProfileNavigationDestination.Back)
            },
            onError = ::onSaveError,
            onTerminate = {
                uiState.confirmButton.isLoading = false
                updateConfirmButton()
            },
        )
    }

    private fun validate(name: FullName): Boolean {
        return uiState.name.validate(
            SettingsRes.strings.settings_edit_profile_name_error.desc()
                .takeIf { !validateName(name.firstName).isValid },
        )
    }

    private fun onSaveError(error: Throwable) {
        when (error) {
            is EditProfile.Error.ContactAlreadyTaken -> {
                showErrorMessage(SettingsRes.strings.settings_edit_profile_email_taken_error.desc())
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

    private fun updateConfirmButton() {
        uiState.confirmButton.isEnabled = savedName != uiState.name.text && uiState.name.isValid
    }
}
