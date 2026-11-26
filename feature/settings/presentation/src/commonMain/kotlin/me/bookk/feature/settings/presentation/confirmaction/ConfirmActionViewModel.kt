package me.bookk.feature.settings.presentation.confirmaction

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.domain.launch.ConfirmableAction
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.error.PresentationNotification.GlobalMessage
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.settings.domain.api.CompleteTelegramConfirmation
import me.bookk.feature.settings.presentation.ClientSettingsStateFactory
import org.koin.core.annotation.InjectedParam

class ConfirmActionViewModel(
    @InjectedParam private val action: ConfirmableAction,
    @InjectedParam private val initData: String,
    private val completeConfirmation: CompleteTelegramConfirmation,
    stateFactory: ClientSettingsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: ConfirmActionState = stateFactory.createConfirmActionState().setup()

    private fun ConfirmActionState.setup() = apply {
        appBar.size = TopBarSize.LARGE
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(ConfirmActionDestinations.Back)
        }
        when (action) {
            ConfirmableAction.DELETE_ACCOUNT -> showAccountDeletion()
            ConfirmableAction.EXPORT_PERSONAL_DATA -> showPersonalDataExport()
        }
        mainButton.text = SettingsRes.strings.settings_confirm_action_button.desc()
        mainButton.onClick = weakVMClosure { it.onMainButtonClick() }
    }

    private fun ConfirmActionState.showAccountDeletion() {
        appBar.title = SettingsRes.strings.settings_confirm_delete_account_title.desc()
        message = SettingsRes.strings.settings_delete_account_confirmation.desc()
        icon = DesignSystem.images.material_delete
        iconColor = ColorToken.Error
        isDestructive = true
    }

    private fun ConfirmActionState.showPersonalDataExport() {
        appBar.title = SettingsRes.strings.settings_confirm_export_title.desc()
        message = SettingsRes.strings.settings_confirm_export_message.desc()
        icon = DesignSystem.images.export_document
        iconColor = ColorToken.ActionText
        isDestructive = false
    }

    private fun onMainButtonClick() {
        if (uiState.error == null) {
            confirm()
        } else {
            uiState.navigation.push(ConfirmActionDestinations.Settings)
        }
    }

    private fun confirm() {
        launch(
            key = CONFIRM_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { uiState.mainButton.startLoading() },
            call = { completeConfirmation(action, initData) },
            onComplete = { onConfirmed() },
            onError = { onConfirmationFailed(it) },
            onTerminate = { uiState.mainButton.stopLoading() },
        )
    }

    private fun onConfirmed() {
        uiState.notifications.add(PresentationNotification.SuccessHaptic)
        when (action) {
            ConfirmableAction.DELETE_ACCOUNT -> uiState.notifications.add(
                GlobalMessage(SettingsRes.strings.settings_delete_account_success_message.desc()),
            )

            ConfirmableAction.EXPORT_PERSONAL_DATA -> uiState.navigation.push(
                ConfirmActionDestinations.PersonalDataExport,
            )
        }
    }

    private fun onConfirmationFailed(error: Throwable) {
        val message = (error as? CompleteTelegramConfirmation.Error)?.message()
        if (message == null) {
            uiState.notifications.add(error.notification())
        } else {
            showFailure(message)
        }
    }

    private fun CompleteTelegramConfirmation.Error.message(): StringResource {
        return when (this) {
            is CompleteTelegramConfirmation.Error.ConfirmationExpired -> {
                SettingsRes.strings.settings_confirm_action_expired
            }

            is CompleteTelegramConfirmation.Error.TelegramAccountMismatch -> {
                SettingsRes.strings.settings_confirm_action_other_account
            }

            is CompleteTelegramConfirmation.Error.TelegramDataInvalid -> {
                SettingsRes.strings.settings_confirm_action_reopen
            }

            is CompleteTelegramConfirmation.Error.ExportTooFrequent -> {
                SettingsRes.strings.settings_export_too_frequent
            }
        }
    }

    private fun showFailure(message: StringResource) {
        uiState.error = message.desc()
        uiState.isDestructive = false
        uiState.mainButton.text = SettingsRes.strings.settings_confirm_action_open_settings.desc()
    }

    private companion object {
        const val CONFIRM_KEY = "confirmAction"
    }
}
