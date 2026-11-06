package me.bookk.feature.settings.presentation.export

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
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
import me.bookk.feature.settings.domain.api.DownloadPersonalDataExport
import me.bookk.feature.settings.domain.api.GetPersonalDataExport
import me.bookk.feature.settings.domain.api.StartPersonalDataExport
import me.bookk.feature.settings.presentation.SettingsStateFactory
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class PersonalDataExportViewModel(
    private val getExport: GetPersonalDataExport,
    private val startExport: StartPersonalDataExport,
    private val downloadExport: DownloadPersonalDataExport,
    private val clock: Clock,
    private val dateLocalizer: DateLocalizer,
    stateFactory: SettingsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createPersonalDataExportState().setup()
    private var exportStatus = DataExportStatus.NONE
    private var hasCheckedOpeningStatus = false

    init {
        observeExport()
    }

    private fun observeExport() {
        getExport.flow()
            .flowOn(DispatcherProvider.io)
            .safeOnEach {
                render(it)
                refreshUnknownOpeningStatus(it)
            }
            .onError {
                uiState.refresh.isVisible = true
                showError(it, SettingsRes.strings.settings_export_load_error)
            }
            .pollWhileRunning()
            .safeOnEach { refreshStatus() }
            .observe()
    }

    private fun refreshUnknownOpeningStatus(export: DataExport?) {
        if (hasCheckedOpeningStatus) return
        hasCheckedOpeningStatus = true
        if (export == null || export.status == DataExportStatus.RUNNING) {
            refreshStatus()
        }
    }

    private fun Flow<DataExport?>.pollWhileRunning(): Flow<Unit> {
        return map { it?.status == DataExportStatus.RUNNING }
            .distinctUntilChanged()
            .transformLatest { isRunning ->
                if (isRunning) {
                    repeat(MAX_STATUS_POLLS) {
                        delay(STATUS_POLL_INTERVAL)
                        emit(Unit)
                    }
                }
            }
    }

    private fun PersonalDataExportState.setup(): PersonalDataExportState = apply {
        appBar.title = SettingsRes.strings.settings_export_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(PersonalDataExportDestinations.Back)
        }
        status = UIExportStatus(DataExportStatus.NONE, canRequest = false)
        request.text = SettingsRes.strings.settings_export_request.desc()
        request.isVisible = false
        request.onClick = weakVMClosure { it.requestExport() }
        download.text = SettingsRes.strings.settings_export_download.desc()
        download.icon = DesignSystem.images.download
        download.isVisible = false
        download.onClick = weakVMClosure { it.downloadFile() }
        refresh.text = SettingsRes.strings.settings_export_refresh.desc()
        refresh.onClick = weakVMClosure { it.refreshStatus() }
    }

    private fun refreshStatus() {
        launch(
            key = OPERATION_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = {
                uiState.error = null
                uiState.request.isEnabled = false
                uiState.download.isEnabled = false
                uiState.refresh.startLoading()
                uiState.refresh.isVisible = true
            },
            call = { getExport.refresh() },
            onError = {
                uiState.refresh.isVisible = true
                showError(it, SettingsRes.strings.settings_export_load_error)
            },
            onTerminate = {
                uiState.refresh.stopLoading()
                uiState.request.isEnabled = true
                uiState.download.isEnabled = true
            },
        )
    }

    private fun render(export: DataExport?) {
        val cooldownEnd = export?.availableAgainAt?.takeIf { it > clock.now() }
        exportStatus = export?.status ?: DataExportStatus.NONE
        uiState.expiry = export?.expiresAt?.let { formatDate(it).desc() }
        uiState.requestAvailability = requestAvailability(exportStatus, cooldownEnd)
        uiState.error = null
        renderStatus(exportStatus, canRequest = cooldownEnd == null)
    }

    private fun requestAvailability(status: DataExportStatus, cooldownEnd: Instant?): StringDesc? {
        return when (status) {
            DataExportStatus.RUNNING,
            DataExportStatus.READY,
            -> SettingsRes.strings.settings_export_request_after_expiry.desc()

            DataExportStatus.NONE,
            DataExportStatus.FAILED,
            -> cooldownEnd?.let {
                SettingsRes.strings.settings_export_cooldown.format(formatDate(it))
            }
        }
    }

    private fun renderStatus(status: DataExportStatus, canRequest: Boolean) {
        val isReady = status == DataExportStatus.READY
        val isRequestable = canRequest && !isReady && status != DataExportStatus.RUNNING
        uiState.status = UIExportStatus(status, canRequest)
        uiState.download.isVisible = isReady
        uiState.request.isVisible = isRequestable
        uiState.refresh.isVisible = !isReady && !isRequestable
    }

    private fun formatDate(instant: Instant): String {
        return dateLocalizer.forStyle(DateStyle.MEDIUM).format(
            instant.toLocalDateTime(TimeZone.currentSystemDefault()),
        )
    }

    private fun requestExport() {
        launch(
            key = OPERATION_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = {
                uiState.error = null
                uiState.request.startLoading()
            },
            call = { startExport() },
            onComplete = {
                uiState.notifications.add(PresentationNotification.SuccessHaptic)
            },
            onError = { handleRequestError(it) },
            onTerminate = { uiState.request.stopLoading() },
        )
    }

    private fun handleRequestError(error: Throwable) {
        when (error) {
            is StartPersonalDataExport.Error.ConfirmationFailed,
            is StartPersonalDataExport.Error.ConfirmationRequired,
            -> {
                uiState.error = SettingsRes.strings.settings_export_confirmation_error.desc()
            }

            is StartPersonalDataExport.Error.TooFrequent -> {
                renderStatus(exportStatus, canRequest = false)
                uiState.error = SettingsRes.strings.settings_export_too_frequent.desc()
            }

            else -> {
                showError(error, SettingsRes.strings.settings_export_request_error)
            }
        }
    }

    private fun downloadFile() {
        launch(
            key = OPERATION_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = {
                uiState.error = null
                uiState.download.startLoading()
            },
            call = { downloadExport() },
            onComplete = {
                uiState.navigation.push(PersonalDataExportDestinations.Share(it.toString()))
            },
            onError = {
                if (it is DownloadPersonalDataExport.Error.NotReady) {
                    renderStatus(DataExportStatus.NONE, canRequest = false)
                    uiState.error = SettingsRes.strings.settings_export_unavailable.desc()
                } else {
                    showError(it, SettingsRes.strings.settings_export_download_error)
                }
            },
            onTerminate = { uiState.download.stopLoading() },
        )
    }

    private fun showError(error: Throwable, message: StringResource) {
        when (error) {
            is Error.Ignore, is CancellationException -> Unit

            is Error.Unauthorized, is Error.BusinessAccessSuspended -> uiState.notifications.add(
                error.notification(),
            )

            else -> uiState.error = message.desc()
        }
    }

    companion object {
        private const val OPERATION_KEY = "personalDataExportOperation"
        private const val MAX_STATUS_POLLS = 30
        private val STATUS_POLL_INTERVAL = 10.seconds
    }
}
