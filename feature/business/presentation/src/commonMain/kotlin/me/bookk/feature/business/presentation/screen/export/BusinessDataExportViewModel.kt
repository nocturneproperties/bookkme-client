package me.bookk.feature.business.presentation.screen.export

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
import me.bookk.android.feature.business.resources.BusinessRes
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
import me.bookk.feature.business.domain.api.DownloadBusinessDataExport
import me.bookk.feature.business.domain.api.GetBusinessDataExport
import me.bookk.feature.business.domain.api.StartBusinessDataExport
import me.bookk.feature.business.presentation.BusinessStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.uuid.Uuid

class BusinessDataExportViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getExport: GetBusinessDataExport,
    private val startExport: StartBusinessDataExport,
    private val downloadExport: DownloadBusinessDataExport,
    private val clock: Clock,
    private val dateLocalizer: DateLocalizer,
    stateFactory: BusinessStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState = stateFactory.createBusinessDataExportState().setup()
    private var exportStatus = DataExportStatus.NONE
    private var hasCheckedOpeningStatus = false
    private val isViewPresented = MutableStateFlow(false)
    private var hasLostAccess = false

    init {
        observeExport()
    }

    override fun onViewPresented() {
        isViewPresented.value = true
    }

    override fun onViewHidden() {
        isViewPresented.value = false
    }

    private fun observeExport() {
        getExport.flow(businessId)
            .flowOn(DispatcherProvider.io)
            .reemitWhenCooldownEnds()
            .safeOnEach { onExportObserved(it) }
            .onError { showLoadError(it) }
            .pollWhileRunning()
            .safeOnEach { refreshStatus() }
            .observe()
    }

    private fun onExportObserved(export: DataExport?) {
        if (hasLostAccess) return
        render(export)
        refreshUnknownOpeningStatus(export)
    }

    private fun refreshUnknownOpeningStatus(export: DataExport?) {
        if (hasCheckedOpeningStatus) return
        hasCheckedOpeningStatus = true
        if (export == null || export.status == DataExportStatus.RUNNING) {
            refreshStatus()
        }
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

    private fun BusinessDataExportState.setup(): BusinessDataExportState = apply {
        appBar.title = BusinessRes.strings.business_export_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(BusinessDataExportDestinations.Back)
        }
        status = UIBusinessExportStatus(DataExportStatus.NONE, canRequest = false)
        request.text = BusinessRes.strings.business_export_request.desc()
        request.isVisible = false
        request.onClick = weakVMClosure { it.requestExport() }
        download.text = BusinessRes.strings.business_export_download.desc()
        download.icon = DesignSystem.images.download
        download.isVisible = false
        download.onClick = weakVMClosure { it.downloadFile() }
        retry.text = DesignSystem.strings.action_retry.desc()
        retry.isVisible = false
        retry.onClick = weakVMClosure { it.refreshStatus() }
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
            call = { getExport.refresh(businessId) },
            onError = { handleRefreshError(it) },
            onTerminate = {
                uiState.retry.stopLoading()
                uiState.request.isEnabled = true
                uiState.download.isEnabled = true
            },
        )
    }

    private fun handleRefreshError(error: Throwable) {
        if (error is GetBusinessDataExport.Error.NotOwner) {
            showLostAccess()
        } else {
            showLoadError(error)
        }
    }

    private fun render(export: DataExport?) {
        val cooldownEnd = export?.availableAgainAt?.takeIf { it > clock.now() }
        exportStatus = export?.status ?: DataExportStatus.NONE
        uiState.expiry = export?.expiresAt?.let { formatDate(it).desc() }
        uiState.requestAvailability = requestAvailability(exportStatus, cooldownEnd)
        uiState.error = null
        uiState.retry.isVisible = false
        renderStatus(exportStatus, canRequest = cooldownEnd == null)
    }

    private fun requestAvailability(status: DataExportStatus, cooldownEnd: Instant?): StringDesc? {
        return when (status) {
            DataExportStatus.RUNNING,
            DataExportStatus.READY,
            -> BusinessRes.strings.business_export_request_after_expiry.desc()

            DataExportStatus.NONE,
            DataExportStatus.FAILED,
            -> cooldownEnd?.let {
                BusinessRes.strings.business_export_cooldown.format(formatDate(it))
            }
        }
    }

    private fun renderStatus(status: DataExportStatus, canRequest: Boolean) {
        val isReady = status == DataExportStatus.READY
        val isRequestable = canRequest && !isReady && status != DataExportStatus.RUNNING
        uiState.status = UIBusinessExportStatus(status, canRequest)
        uiState.download.isVisible = isReady
        uiState.request.isVisible = isRequestable
    }

    private fun showLoadError(error: Throwable) {
        uiState.retry.isVisible = exportStatus != DataExportStatus.RUNNING
        showError(error, BusinessRes.strings.business_export_load_error)
    }

    private fun showRetryableError(message: StringDesc) {
        uiState.retry.isVisible = true
        uiState.error = message
    }

    private fun showLostAccess() {
        hasLostAccess = true
        uiState.status = UIBusinessExportStatus(DataExportStatus.NONE, canRequest = false)
        uiState.expiry = null
        uiState.requestAvailability = null
        uiState.request.isVisible = false
        uiState.download.isVisible = false
        uiState.retry.isVisible = false
        uiState.error = BusinessRes.strings.business_export_not_owner.desc()
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
            call = { startExport(businessId) },
            onComplete = {
                uiState.notifications.add(PresentationNotification.SuccessHaptic)
            },
            onError = { handleRequestError(it) },
            onTerminate = { uiState.request.stopLoading() },
        )
    }

    private fun handleRequestError(error: Throwable) {
        when (error) {
            is StartBusinessDataExport.Error.ConfirmationFailed,
            is StartBusinessDataExport.Error.ConfirmationRequired,
            -> {
                uiState.error = BusinessRes.strings.business_export_confirmation_error.desc()
            }

            is StartBusinessDataExport.Error.TooFrequent -> {
                renderStatus(exportStatus, canRequest = false)
                showRetryableError(BusinessRes.strings.business_export_too_frequent.desc())
            }

            is StartBusinessDataExport.Error.NotOwner -> {
                showLostAccess()
            }

            else -> {
                showError(error, BusinessRes.strings.business_export_request_error)
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
            call = { downloadExport(businessId) },
            onComplete = {
                uiState.navigation.push(BusinessDataExportDestinations.Share(it.toString()))
            },
            onError = { handleDownloadError(it) },
            onTerminate = { uiState.download.stopLoading() },
        )
    }

    private fun handleDownloadError(error: Throwable) {
        if (error is DownloadBusinessDataExport.Error.NotReady) {
            renderStatus(DataExportStatus.NONE, canRequest = false)
            showRetryableError(BusinessRes.strings.business_export_unavailable.desc())
        } else {
            showError(error, BusinessRes.strings.business_export_download_error)
        }
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
        private const val OPERATION_KEY = "businessDataExportOperation"
        private val STATUS_POLL_INTERVAL = 10.seconds
    }
}
