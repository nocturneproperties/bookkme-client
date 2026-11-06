package me.bookk.feature.settings.presentation.dashboard

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.flowOn
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.action
import me.bookk.feature.settings.domain.api.GetLegalDocumentStatuses
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.UpdateColorScheme
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.dashboard.AppearanceSection.UIColorScheme

class SettingsDashboardViewModel(
    private val getSettings: GetSettings,
    private val getLegalDocumentStatuses: GetLegalDocumentStatuses,
    private val updateColorScheme: UpdateColorScheme,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: SettingsState = settingsStateFactory.createSettingsState(createInitData()).setup()

    init {
        observeLegalDocumentStatuses()
    }

    private fun SettingsState.setup(): SettingsState = apply {
        account.dataExport.text = SettingsRes.strings.settings_export_title.desc()
        account.privacy.text = SettingsRes.strings.settings_privacy_title.desc()
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
        refreshLegalDocumentStatuses()
    }

    private fun observeLegalDocumentStatuses() {
        getLegalDocumentStatuses.flow()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { uiState.account.privacyStatus = it.privacyStatus() }
            .observe()
    }

    private fun refreshLegalDocumentStatuses() {
        launch(
            key = REFRESH_LEGAL_DOCUMENTS_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            call = { getLegalDocumentStatuses.refresh() },
            onError = { handleError(it) },
        )
    }

    private fun List<LegalDocumentStatus>.privacyStatus(): StringDesc? {
        return if (any { it.acceptedAt == null }) {
            SettingsRes.strings.settings_privacy_action_required.desc()
        } else {
            null
        }
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
        private const val REFRESH_LEGAL_DOCUMENTS_KEY = "refreshLegalDocuments"

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
