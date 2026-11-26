package me.bookk.feature.settings.presentation.confirmaction

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.domain.launch.ConfirmableAction
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.simple
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

    val uiState: ConfirmActionState = stateFactory.createConfirmActionState()

    init {
        confirm()
    }

    private fun confirm() {
        launch(
            key = CONFIRM_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            call = { completeConfirmation(action, initData) },
            onComplete = { uiState.notifications.add(PresentationNotification.SuccessHaptic) },
            onError = { uiState.notifications.add(failureNotification(it)) },
        )
    }

    private fun failureNotification(error: Throwable): PresentationNotification {
        val message = (error as? CompleteTelegramConfirmation.Error)?.message()
            ?: return error.notification()
        return PresentationNotification.Message.simple(message.desc())
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

    private companion object {
        const val CONFIRM_KEY = "confirmAction"
    }
}
