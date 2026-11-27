package me.bookk.feature.settings.presentation.deletionbanner

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.flowOn
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.domain.entity.Error
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.settings.domain.api.CancelAccountDeletion
import me.bookk.feature.settings.domain.api.GetAccountDeletion
import me.bookk.feature.settings.presentation.SettingsStateFactory
import kotlin.time.Instant

class AccountDeletionBannerViewModel(
    private val getAccountDeletion: GetAccountDeletion,
    private val cancelAccountDeletion: CancelAccountDeletion,
    private val dateLocalizer: DateLocalizer,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: AccountDeletionBannerState =
        settingsStateFactory.createAccountDeletionBannerState().setup()

    init {
        observeDeletion()
    }

    fun onAppResumed() {
        refreshDeletion()
    }

    private fun AccountDeletionBannerState.setup() = apply {
        isVisible = false
        cancelButton.text = SettingsRes.strings.settings_account_deletion_banner_cancel.desc()
        cancelButton.onClick = weakVMClosure { it.cancelDeletion() }
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
            onError = { onRefreshFailed(it) },
        )
    }

    private fun onRefreshFailed(error: Throwable) {
        when (error) {
            is Error.Unauthorized -> {
                uiState.notifications.add(PresentationNotification.Unauthorized)
            }

            else -> {
                handleError(error)
            }
        }
    }

    private fun renderDeletion(deletesAt: Instant?) {
        uiState.isVisible = deletesAt != null
        deletesAt?.let { uiState.message = deletionMessage(it) }
    }

    private fun deletionMessage(deletesAt: Instant): StringDesc {
        val date = dateLocalizer.forStyle(DateStyle.MEDIUM).format(
            deletesAt.toLocalDateTime(TimeZone.currentSystemDefault()),
        )
        return SettingsRes.strings.settings_account_deletion_banner.format(date)
    }

    private fun cancelDeletion() {
        launch(
            key = CANCEL_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { uiState.cancelButton.startLoading() },
            call = { cancelAccountDeletion() },
            onComplete = { onDeletionCancelled() },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { uiState.cancelButton.stopLoading() },
        )
    }

    private fun onDeletionCancelled() {
        uiState.notifications.add(
            PresentationNotification.GlobalMessage(
                text = SettingsRes.strings.settings_account_deletion_cancelled.desc(),
            ),
        )
        uiState.notifications.add(PresentationNotification.SuccessHaptic)
    }

    private companion object {
        const val REFRESH_KEY = "accountDeletionStatus"
        const val CANCEL_KEY = "cancelAccountDeletion"
    }
}
