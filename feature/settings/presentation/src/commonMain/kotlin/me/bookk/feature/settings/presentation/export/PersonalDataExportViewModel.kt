package me.bookk.feature.settings.presentation.export

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
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
import me.bookk.feature.settings.presentation.confirmation.ChatConfirmationRequest
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

    val uiState: PersonalDataExportState = stateFactory.createPersonalDataExportState().setup()

    private val exportRequest = ChatConfirmationRequest(
        viewModel = this,
        key = OPERATION_KEY,
        state = uiState.chatConfirmation,
        button = uiState.request,
        notifications = uiState.notifications,
        action = { startExport() },
        onCompleted = { uiState.notifications.add(PresentationNotification.SuccessHaptic) },
        onError = { handleRequestError(it) },
    )

    private var exportStatus = DataExportStatus.NONE
    private var hasCheckedOpeningStatus = false
    private val isViewPresented = MutableStateFlow(false)

    init {
        observeExport()
    }

    override fun onViewPresented() {
        isViewPresented.value = true
    }

    override fun onViewHidden() {
        isViewPresented.value = false
    }

    private fun PersonalDataExportState.setup() = apply {
        appBar.title = SettingsRes.strings.settings_export_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(PersonalDataExportDestinations.Back)
        }
        status = UIExportStatus(UIExportStage.REQUEST, canRequest = false)
        request.text = SettingsRes.strings.settings_export_request.desc()
        request.isVisible = false
        request.onClick = weakVMClosure { it.requestExport() }
        chatConfirmation.onWriteAccessAnswered = weakVMClosure { vm, isGranted: Boolean ->
            vm.exportRequest.onWriteAccessAnswered(isGranted)
        }
        download.text = SettingsRes.strings.settings_export_download.desc()
        download.icon = DesignSystem.images.download
        download.isVisible = false
        download.onClick = weakVMClosure { it.downloadFile() }
        retry.text = DesignSystem.strings.action_retry.desc()
        retry.isVisible = false
        retry.onClick = weakVMClosure { it.refreshStatus() }
    }

    private fun observeExport() {
        getExport.flow()
            .flowOn(DispatcherProvider.io)
            .reemitWhenCooldownEnds()
            .safeOnEach {
                render(it)
                refreshUnknownOpeningStatus(it)
            }
            .onError { showLoadError(it) }
            .pollWhileRunning()
            .safeOnEach { refreshStatus() }
            .observe()
    }

    private fun Flow<DataExport?>.reemitWhenCooldownEnds(): Flow<DataExport?> {
        return transformLatest { export ->
            emit(export)
            val cooldownLeft = export?.availableAgainAt?.minus(clock.now())
            if (cooldownLeft != null && cooldownLeft.isPositive()) {
                delay(cooldownLeft)
                emit(export)
            }
        }
    }

    private fun render(export: DataExport?) {
        val cooldownEnd = export?.availableAgainAt?.takeIf { it > clock.now() }
        exportStatus = export?.status ?: DataExportStatus.NONE
        uiState.expiry = export?.expiresAt?.let { formatDate(it).desc() }
        uiState.requestAvailability = requestAvailability(exportStatus, cooldownEnd)
        uiState.error = null
        uiState.retry.isVisible = false
        renderStatus(stageOf(exportStatus, cooldownEnd), canRequest = cooldownEnd == null)
    }

    private fun requestAvailability(status: DataExportStatus, cooldownEnd: Instant?): StringDesc? {
        return when (status) {
            DataExportStatus.RUNNING,
            DataExportStatus.READY,
            -> {
                SettingsRes.strings.settings_export_request_after_expiry.desc()
            }

            DataExportStatus.NONE,
            DataExportStatus.FAILED,
            -> {
                cooldownEnd?.let {
                    SettingsRes.strings.settings_export_cooldown.format(formatDate(it))
                }
            }
        }
    }

    private fun stageOf(status: DataExportStatus, cooldownEnd: Instant?): UIExportStage {
        return when (status) {
            DataExportStatus.NONE -> {
                if (cooldownEnd == null) UIExportStage.REQUEST else UIExportStage.EXPIRED
            }

            DataExportStatus.RUNNING -> {
                UIExportStage.PREPARING
            }

            DataExportStatus.READY -> {
                UIExportStage.READY
            }

            DataExportStatus.FAILED -> {
                UIExportStage.FAILED
            }
        }
    }

    private fun renderStatus(stage: UIExportStage, canRequest: Boolean) {
        val isReady = stage == UIExportStage.READY
        val isRequestable = canRequest && stage in REQUESTABLE_STAGES
        uiState.status = UIExportStatus(stage, canRequest)
        uiState.download.isVisible = isReady
        uiState.request.isVisible = isRequestable
    }

    private fun refreshUnknownOpeningStatus(export: DataExport?) {
        if (hasCheckedOpeningStatus) return
        hasCheckedOpeningStatus = true
        if (export == null || export.status == DataExportStatus.RUNNING) {
            refreshStatus()
        }
    }

    private fun showLoadError(error: Throwable) {
        uiState.retry.isVisible = exportStatus != DataExportStatus.RUNNING
        showError(error, SettingsRes.strings.settings_export_load_error)
    }

    private fun Flow<DataExport?>.pollWhileRunning(): Flow<Unit> {
        return map { it?.status == DataExportStatus.RUNNING }
            .combine(isViewPresented) { isRunning, isPresented -> isRunning && isPresented }
            .distinctUntilChanged()
            .transformLatest { shouldPoll ->
                while (shouldPoll) {
                    delay(STATUS_POLL_INTERVAL)
                    emit(Unit)
                }
            }
    }

    private fun refreshStatus() {
        launch(
            key = OPERATION_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = {
                uiState.request.isEnabled = false
                uiState.download.isEnabled = false
                uiState.retry.startLoading()
            },
            call = { getExport.refresh() },
            onError = { showLoadError(it) },
            onTerminate = {
                uiState.retry.stopLoading()
                uiState.request.isEnabled = exportRequest.canSend()
                uiState.download.isEnabled = true
            },
        )
    }

    private fun requestExport() {
        uiState.error = null
        exportRequest.send()
    }

    private fun handleRequestError(error: Throwable) {
        when (error) {
            is StartPersonalDataExport.Error.ConfirmationFailed,
            is StartPersonalDataExport.Error.ConfirmationRequired,
            -> {
                uiState.error = exportConfirmationError.desc()
            }

            is StartPersonalDataExport.Error.TooFrequent -> {
                renderStatus(stageOf(exportStatus, cooldownEnd = null), canRequest = false)
                showRetryableError(SettingsRes.strings.settings_export_too_frequent.desc())
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
            onError = { handleDownloadError(it) },
            onTerminate = { uiState.download.stopLoading() },
        )
    }

    private fun handleDownloadError(error: Throwable) {
        when (error) {
            is DownloadPersonalDataExport.Error.NotReady -> {
                renderStatus(UIExportStage.REQUEST, canRequest = false)
                showRetryableError(SettingsRes.strings.settings_export_unavailable.desc())
            }

            else -> {
                showError(error, SettingsRes.strings.settings_export_download_error)
            }
        }
    }

    private fun showRetryableError(message: StringDesc) {
        uiState.retry.isVisible = true
        uiState.error = message
    }

    private fun showError(error: Throwable, message: StringResource) {
        when (error) {
            is Error.Ignore, is CancellationException -> {}

            is Error.Unauthorized, is Error.BusinessAccessSuspended -> {
                uiState.notifications.add(error.notification())
            }

            else -> {
                uiState.error = message.desc()
            }
        }
    }

    private fun formatDate(instant: Instant): String {
        return dateLocalizer.forStyle(DateStyle.MEDIUM).format(
            instant.toLocalDateTime(TimeZone.currentSystemDefault()),
        )
    }

    companion object {
        private const val OPERATION_KEY = "personalDataExportOperation"
        private val STATUS_POLL_INTERVAL = 10.seconds
        private val REQUESTABLE_STAGES = setOf(UIExportStage.REQUEST, UIExportStage.FAILED)
    }
}
