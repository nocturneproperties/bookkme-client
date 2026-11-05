package me.bookk.feature.settings.presentation.dashboard

import dev.icerock.moko.resources.desc.desc
import library.device.api.DeviceFacade
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.action
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.settings.domain.api.AcceptLegalDocuments
import me.bookk.feature.settings.domain.api.GetLegalDocumentStatuses
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.UpdateColorScheme
import me.bookk.feature.settings.domain.api.entity.AcceptedLegalDocuments
import me.bookk.feature.settings.domain.api.entity.LegalDocument
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.dashboard.AppearanceSection.UIColorScheme

class SettingsDashboardViewModel(
    private val getSettings: GetSettings,
    private val getLegalDocumentStatuses: GetLegalDocumentStatuses,
    private val acceptLegalDocuments: AcceptLegalDocuments,
    private val updateColorScheme: UpdateColorScheme,
    private val deviceFacade: DeviceFacade,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    private var legalStatuses: List<LegalDocumentStatus> = emptyList()

    val uiState: SettingsState = settingsStateFactory.createSettingsState(createInitData()).setup()

    init {
        loadLegalDocuments()
    }

    private fun SettingsState.setup(): SettingsState = apply {
        legal.title = SettingsRes.strings.settings_legal_title.desc()
        legal.accept.text = SettingsRes.strings.settings_legal_accept.desc()
        legal.accept.isVisible = false
        legal.accept.isEnabled = false
        legal.retry.text = SettingsRes.strings.settings_legal_retry.desc()
        legal.retry.isVisible = false
        legal.retry.onClick = weakVMClosure { it.loadLegalDocuments() }
        appBar.actions.replace(
            listOf(
                appBar.action(
                    contentDescription = SettingsRes.strings.settings_profile_edit.desc(),
                    onClick = weakVMClosure { it.onEditClick() },
                ),
            ),
        )
    }

    override fun onViewPresented() {
        loadSettings()
    }

    fun onSchemeSelected(scheme: UIColorScheme) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { updateColorScheme.invoke(scheme.toDomain()) },
            onComplete = {
                uiState.notification.add(PresentationNotification.SuccessHaptic)
                uiState.appearance.colorScheme = scheme
            },
            onError = { uiState.notification.add(errorMapper.mapToNotification(it)) },
        )
    }

    private fun loadLegalDocuments() {
        if (uiState.legal.isLoading || uiState.legal.accept.isLoading) return
        launch(
            launchIn = DispatcherProvider.io,
            onStart = {
                uiState.legal.isLoading = true
                if (uiState.legal.documents.isEmpty()) {
                    uiState.legal.message = null
                }
                uiState.legal.retry.isVisible = false
                uiState.legal.accept.isEnabled = false
                uiState.legal.accept.onClick = null
                legalStatuses = emptyList()
            },
            call = { getLegalDocumentStatuses() },
            onComplete = { renderLegalDocuments(it) },
            onError = {
                uiState.legal.documents = emptyList()
                uiState.legal.accept.isVisible = false
                uiState.legal.message = SettingsRes.strings.settings_legal_load_error.desc()
                uiState.legal.retry.isVisible = true
            },
            onTerminate = {
                uiState.legal.isLoading = false
                updateLegalAcceptanceEnabled()
            },
        )
    }

    private fun renderLegalDocuments(statuses: List<LegalDocumentStatus>) {
        legalStatuses = statuses
        uiState.legal.documents = statuses.map { status ->
            LegalDocumentItem(
                id = status.current.document.name,
                title = when (status.current.document) {
                    LegalDocument.PRIVACY_POLICY ->
                        SettingsRes.strings.settings_support_privacy_label.desc()

                    LegalDocument.TERMS_OF_SERVICE ->
                        SettingsRes.strings.settings_support_terms_label.desc()
                },
                version = status.current.version,
                status = if (status.acceptedAt == null) {
                    SettingsRes.strings.settings_legal_pending.desc()
                } else {
                    SettingsRes.strings.settings_legal_accepted.desc()
                },
                statusColor = if (status.acceptedAt == null) {
                    ColorToken.ActionText
                } else {
                    ColorToken.SecondaryText
                },
                onClick = weakVMClosure { it.deviceFacade.openUrlPreview(status.current.url) },
            )
        }
        uiState.legal.message = when {
            statuses.isEmpty() -> SettingsRes.strings.settings_legal_empty.desc()

            statuses.any { it.acceptedAt == null } ->
                SettingsRes.strings.settings_legal_consent.desc()

            else -> null
        }
        uiState.legal.accept.isVisible = statuses.any { it.acceptedAt == null }
        uiState.legal.accept.onClick = weakVMClosure { it.acceptLegalDocuments(statuses) }
    }

    private fun acceptLegalDocuments(displayedStatuses: List<LegalDocumentStatus>) {
        if (legalStatuses !== displayedStatuses || !uiState.legal.accept.isEnabled) return
        val pending = displayedStatuses.filter { it.acceptedAt == null }
        if (pending.isEmpty()) return
        val documents = AcceptedLegalDocuments(
            privacyPolicy = pending.find {
                it.current.document == LegalDocument.PRIVACY_POLICY
            }?.current?.version,
            termsOfService = pending.find {
                it.current.document == LegalDocument.TERMS_OF_SERVICE
            }?.current?.version,
        )
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.legal.accept.startLoading() },
            call = { acceptLegalDocuments(documents) },
            onComplete = {
                uiState.notification.add(PresentationNotification.SuccessHaptic)
                uiState.legal.accept.stopLoading(enable = false)
                loadLegalDocuments()
            },
            onError = {
                when (it) {
                    is AcceptLegalDocuments.Error.UnknownVersion -> {
                        uiState.notification.add(
                            PresentationNotification.GlobalMessage(
                                text = SettingsRes.strings.settings_legal_changed.desc(),
                                state = PresentationNotification.GlobalMessage.State.INFO,
                            ),
                        )
                        uiState.legal.accept.stopLoading(enable = false)
                        loadLegalDocuments()
                    }

                    else -> uiState.notification.add(it.notification())
                }
            },
            onTerminate = {
                uiState.legal.accept.isLoading = false
                updateLegalAcceptanceEnabled()
            },
        )
    }

    private fun updateLegalAcceptanceEnabled() {
        uiState.legal.accept.isEnabled = !uiState.legal.isLoading &&
            !uiState.legal.accept.isLoading && legalStatuses.any { it.acceptedAt == null }
    }

    fun onLogOutClick() {
        uiState.notification.add(
            PresentationNotification.Message(
                title = SettingsRes.strings.settings_account_logout_label.desc(),
                message = SettingsRes.strings.settings_account_logout_message.desc(),
                buttons = listOf(
                    ButtonDescriptor(
                        text = DesignSystem.strings.action_cancel.desc(),
                        actionType = ActionType.CANCEL,
                    ),
                    ButtonDescriptor(
                        text = DesignSystem.strings.action_confirm.desc(),
                        actionType = ActionType.NEGATIVE,
                        onClick = ::logout,
                    ),
                ),
            ),
        )
    }

    private fun onEditClick() {
        uiState.navigation.push(SettingsDashboardDestination.EditProfile)
    }

    private fun logout() {
        uiState.notification.add(PresentationNotification.Unauthorized)
    }

    private fun loadSettings() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getSettings() },
            onComplete = {
                uiState.appearance.colorScheme = UIColorScheme.from(it.colorScheme)
                uiState.profile.name = it.profile.firstName.desc()
                uiState.profile.lastName = it.profile.lastName.desc()
                uiState.profile.email = it.profile.email.desc()
            },
            onError = { uiState.notification.add(errorMapper.mapToNotification(it)) },
        )
    }

    companion object {
        fun createInitData() = SettingsState.InitData(
            appearance = AppearanceSection.InitData(
                title = SettingsRes.strings.settings_appearance_title.desc(),
            ),
            account = AccountSection.InitData(
                title = SettingsRes.strings.settings_account_title.desc(),
                passkeyLabel = SettingsRes.strings.settings_account_passkey_label.desc(),
                logoutLabel = SettingsRes.strings.settings_account_logout_label.desc(),
                deleteAccountLabel = SettingsRes.strings.settings_account_delete_acc_label.desc(),
                notificationsLabel = SettingsRes.strings.settings_account_notifications_lable
                    .desc(),
            ),
            support = SupportSection.InitData(
                title = SettingsRes.strings.settings_support_title.desc(),
                contactLabel = SettingsRes.strings.settings_support_contact_label.desc(),
                featureLabel = SettingsRes.strings.settings_support_feature_label.desc(),
                termsLabel = SettingsRes.strings.settings_support_terms_label.desc(),
                policyLabel = SettingsRes.strings.settings_support_privacy_label.desc(),
                reportErrorLabel = SettingsRes.strings.settings_support_report_label.desc(),
            ),
        )
    }
}
