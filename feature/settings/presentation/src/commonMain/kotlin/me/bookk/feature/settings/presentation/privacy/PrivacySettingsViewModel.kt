package me.bookk.feature.settings.presentation.privacy

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.flowOn
import library.device.api.DeviceFacade
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.settings.domain.api.AcceptLegalDocuments
import me.bookk.feature.settings.domain.api.GetCrashReportingEnabled
import me.bookk.feature.settings.domain.api.GetLegalDocumentStatuses
import me.bookk.feature.settings.domain.api.UpdateCrashReportingEnabled
import me.bookk.feature.settings.domain.api.entity.AcceptedLegalDocuments
import me.bookk.feature.settings.domain.api.entity.LegalDocument
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.presentation.SettingsStateFactory

class PrivacySettingsViewModel(
    private val getLegalDocumentStatuses: GetLegalDocumentStatuses,
    private val acceptLegalDocuments: AcceptLegalDocuments,
    private val getCrashReportingEnabled: GetCrashReportingEnabled,
    private val updateCrashReportingEnabled: UpdateCrashReportingEnabled,
    private val deviceFacade: DeviceFacade,
    stateFactory: SettingsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: PrivacySettingsState = stateFactory.createPrivacySettingsState().setup()

    init {
        observeLegalDocuments()
        loadCrashReporting()
    }

    private fun PrivacySettingsState.setup(): PrivacySettingsState = apply {
        appBar.title = SettingsRes.strings.settings_privacy_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(PrivacySettingsDestinations.Back)
        }
        diagnosticsTitle = SettingsRes.strings.settings_privacy_diagnostics_title.desc()
        crashReporting.text = SettingsRes.strings.settings_privacy_crash_reports_label.desc()
        crashReportingDescription =
            SettingsRes.strings.settings_privacy_crash_reports_description.desc()
        crashReporting.onCheckedChange = weakVMClosure { vm, enabled ->
            vm.onCrashReportingChanged(enabled)
        }
        legal.title = SettingsRes.strings.settings_legal_title.desc()
        legal.accept.text = SettingsRes.strings.settings_legal_accept.desc()
        legal.accept.isVisible = false
        legal.retry.text = SettingsRes.strings.settings_legal_retry.desc()
        legal.retry.isVisible = false
        legal.retry.onClick = weakVMClosure { it.loadLegalDocuments() }
        renderLegalDocuments(emptyList())
    }

    override fun onViewPresented() {
        loadLegalDocuments()
    }

    private fun loadCrashReporting() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getCrashReportingEnabled() },
            onComplete = { uiState.crashReporting.isChecked = it },
            onError = { uiState.notifications.add(it.notification()) },
        )
    }

    private fun onCrashReportingChanged(enabled: Boolean) {
        launch(
            key = UPDATE_CRASH_REPORTING_KEY,
            launchBehaviour = LaunchBehaviour.DropOldest,
            launchIn = DispatcherProvider.io,
            onStart = { uiState.crashReporting.isChecked = enabled },
            call = { updateCrashReportingEnabled(enabled) },
            onComplete = { uiState.notifications.add(PresentationNotification.SuccessHaptic) },
            onError = {
                uiState.crashReporting.isChecked = !enabled
                uiState.notifications.add(it.notification())
            },
        )
    }

    private fun observeLegalDocuments() {
        getLegalDocumentStatuses.flow()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { uiState.renderLegalDocuments(it) }
            .onError { showLegalDocumentsError() }
            .observe()
    }

    private fun showLegalDocumentsError() {
        if (uiState.legal.documents.all { it.onClick == null }) {
            uiState.legal.message = SettingsRes.strings.settings_legal_load_error.desc()
        }
        uiState.legal.retry.isVisible = true
    }

    private fun loadLegalDocuments() {
        launch(
            key = LOAD_LEGAL_DOCUMENTS_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = {
                uiState.legal.isLoading = true
                uiState.legal.retry.isVisible = false
            },
            call = { getLegalDocumentStatuses.refresh() },
            onError = { showLegalDocumentsError() },
            onTerminate = {
                uiState.legal.isLoading = false
            },
        )
    }

    private fun PrivacySettingsState.renderLegalDocuments(statuses: List<LegalDocumentStatus>) {
        legal.documents = LegalDocument.entries.map { document ->
            createLegalDocumentItem(document, statuses.find { it.current.document == document })
        }
        val hasPendingDocuments = statuses.any { it.acceptedAt == null }
        legal.message = if (hasPendingDocuments) {
            SettingsRes.strings.settings_legal_consent.desc()
        } else {
            null
        }
        legal.accept.isVisible = hasPendingDocuments
        legal.accept.onClick = weakVMClosure { it.acceptLegalDocuments(statuses) }
    }

    private fun createLegalDocumentItem(
        document: LegalDocument,
        status: LegalDocumentStatus?,
    ): LegalDocumentItem {
        return LegalDocumentItem(
            id = document.name,
            title = document.title(),
            version = status?.current?.version,
            status = status.acceptanceDescription(),
            statusColor = status.acceptanceColor(),
            onClick = status?.let { loadedStatus ->
                weakVMClosure { it.deviceFacade.openUrlPreview(loadedStatus.current.url) }
            },
        )
    }

    private fun LegalDocument.title(): StringDesc {
        return when (this) {
            LegalDocument.PRIVACY_POLICY -> {
                SettingsRes.strings.settings_support_privacy_label.desc()
            }

            LegalDocument.TERMS_OF_SERVICE -> {
                SettingsRes.strings.settings_support_terms_label.desc()
            }
        }
    }

    private fun LegalDocumentStatus?.acceptanceDescription(): StringDesc {
        return when {
            this == null -> SettingsRes.strings.settings_legal_unknown.desc()
            acceptedAt == null -> SettingsRes.strings.settings_legal_pending.desc()
            else -> SettingsRes.strings.settings_legal_accepted.desc()
        }
    }

    private fun LegalDocumentStatus?.acceptanceColor(): ColorToken {
        return if (this != null && acceptedAt == null) {
            ColorToken.ActionText
        } else {
            ColorToken.SecondaryText
        }
    }

    private fun acceptLegalDocuments(displayedStatuses: List<LegalDocumentStatus>) {
        val pending = displayedStatuses.filter { it.acceptedAt == null }
        val documents = AcceptedLegalDocuments(
            privacyPolicy = pending.find {
                it.current.document == LegalDocument.PRIVACY_POLICY
            }?.current?.version,
            termsOfService = pending.find {
                it.current.document == LegalDocument.TERMS_OF_SERVICE
            }?.current?.version,
        )
        launch(
            key = ACCEPT_LEGAL_DOCUMENTS_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            onStart = { uiState.legal.accept.startLoading() },
            call = { acceptLegalDocuments(documents) },
            onComplete = {
                uiState.notifications.add(PresentationNotification.SuccessHaptic)
                loadLegalDocuments()
            },
            onError = {
                when (it) {
                    is AcceptLegalDocuments.Error.UnknownVersion -> {
                        uiState.notifications.add(
                            PresentationNotification.GlobalMessage(
                                text = SettingsRes.strings.settings_legal_changed.desc(),
                                state = PresentationNotification.GlobalMessage.State.INFO,
                            ),
                        )
                        loadLegalDocuments()
                    }

                    else -> {
                        uiState.notifications.add(it.notification())
                    }
                }
            },
            onTerminate = { uiState.legal.accept.stopLoading() },
        )
    }

    companion object {
        private const val LOAD_LEGAL_DOCUMENTS_KEY = "loadLegalDocuments"
        private const val ACCEPT_LEGAL_DOCUMENTS_KEY = "acceptLegalDocuments"
        private const val UPDATE_CRASH_REPORTING_KEY = "updateCrashReporting"
    }
}
