package me.bookk.feature.settings.presentation.accdelete

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.feature.settings.domain.api.GetAccountDeletion
import me.bookk.feature.settings.domain.api.RequestAccountDeletion
import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.accdelete.DeleteAccountNavigationDestination.Back
import me.bookk.feature.settings.presentation.confirmation.ChatConfirmationRequest
import kotlin.time.Instant

class DeleteAccountViewModel(
    private val requestAccountDeletion: RequestAccountDeletion,
    private val getAccountDeletion: GetAccountDeletion,
    private val dateLocalizer: DateLocalizer,
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
        action = { requestAccountDeletion() },
        onCompleted = { onAccountDeleted() },
        onError = { uiState.notifications.add(deletionFailure(it)) },
        isActionAllowed = { uiState.confirmation.isChecked },
    )

    init {
        observeDeletion()
        refreshDeletion()
    }

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

    private fun observeDeletion() {
        getAccountDeletion.flow()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderDeletion(it) }
            .observe()
    }

    private fun refreshDeletion() {
        launch(
            key = REFRESH_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            call = { getAccountDeletion.refresh() },
            onError = { handleError(it) },
        )
    }

    private fun renderDeletion(deletesAt: Instant?) {
        val isPending = deletesAt != null
        uiState.pendingDeletion = deletesAt?.let { pendingDeletionMessage(it) }
        uiState.confirmation.isVisible = !isPending
        uiState.deleteButton.isVisible = !isPending
    }

    private fun pendingDeletionMessage(deletesAt: Instant): StringDesc {
        val date = dateLocalizer.forStyle(DateStyle.MEDIUM).format(
            deletesAt.toLocalDateTime(TimeZone.currentSystemDefault()),
        )
        return SettingsRes.strings.settings_delete_account_pending.format(date)
    }

    private fun deletionFailure(error: Throwable): PresentationNotification {
        return when (error) {
            is RequestAccountDeletion.Error.DeletionOnHold -> {
                PresentationNotification.Message.simple(
                    SettingsRes.strings.settings_delete_account_on_hold.desc(),
                )
            }

            else -> {
                error.notification()
            }
        }
    }

    private fun onAgreementChanged(isChecked: Boolean) {
        uiState.confirmation.isChecked = isChecked
        uiState.deleteButton.isEnabled = deletion.canSend()
    }

    private fun onAccountDeleted() {
        uiState.notifications.add(PresentationNotification.SuccessHaptic)
    }

    private companion object {
        const val DELETE_KEY = "deleteAccount"
        const val REFRESH_KEY = "accountDeletionStatus"
    }
}
