package me.bookk.feature.settings.presentation.dashboard

import dev.icerock.moko.resources.desc.desc
import library.device.api.DeviceFacade
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.LogOut
import me.bookk.feature.settings.domain.api.UpdateColorScheme
import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.dashboard.AppearanceSection.UIColorScheme

class SettingsDashboardViewModel(
    private val getSettings: GetSettings,
    private val updateColorScheme: UpdateColorScheme,
    private val deviceFacade: DeviceFacade,
    private val logOut: LogOut,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = settingsStateFactory.createSettingsState(createInitData())

    override fun onViewPresented() {
        loadSettings()
    }

    fun onSchemeSelected(scheme: UIColorScheme) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { updateColorScheme.invoke(scheme.toDomain()) },
            onComplete = { uiState.appearance.colorScheme = scheme },
            onError = { uiState.notification.add(errorMapper.mapToNotification(it)) }
        )
    }

    fun showTerms() {
        deviceFacade.openUrlPreview("https://google.com")
    }

    fun showPolicy() {
        deviceFacade.openUrlPreview("https://google.com")
    }

    fun onLogOutClick() {
        uiState.notification.add(
            PresentationNotification.Message(
                title = SettingsRes.strings.settings_account_logout_label.desc(),
                message = SettingsRes.strings.settings_account_logout_message.desc(),
                buttons = listOf(
                    ButtonDescriptor(
                        text = DesignSystem.strings.action_cancel.desc(),
                        actionType = ButtonDescriptor.ActionType.POSITIVE
                    ),
                    ButtonDescriptor(
                        text = DesignSystem.strings.action_confirm.desc(),
                        actionType = ButtonDescriptor.ActionType.NEGATIVE,
                        onClick = ::logout
                    )
                ),
            )
        )
    }

    private fun logout() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { logOut() },
            onError = { uiState.notification.add(errorMapper.mapToNotification(it)) }
        )
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
            onError = { uiState.notification.add(errorMapper.mapToNotification(it)) }
        )
    }


    companion object {
        fun createInitData() = SettingsState.InitData(
            appearance = AppearanceSection.InitData(
                title = SettingsRes.strings.settings_appearance_title.desc()
            ),
            profile = ProfileSection.InitData(
                title = SettingsRes.strings.settings_profile_title.desc(),
                editButtonText = SettingsRes.strings.settings_profile_edit.desc()
            ),
            account = AccountSection.InitData(
                title = SettingsRes.strings.settings_account_title.desc(),
                passkeyLabel = SettingsRes.strings.settings_account_passkey_label.desc(),
                logoutLabel = SettingsRes.strings.settings_account_logout_label.desc(),
                deleteAccountLabel = SettingsRes.strings.settings_account_delete_acc_label.desc()
            ),
            support = SupportSection.InitData(
                title = SettingsRes.strings.settings_support_title.desc(),
                contactLabel = SettingsRes.strings.settings_support_contact_label.desc(),
                featureLabel = SettingsRes.strings.settings_support_feature_label.desc(),
                termsLabel = SettingsRes.strings.settings_support_terms_label.desc(),
                policyLabel = SettingsRes.strings.settings_support_privacy_label.desc(),
                reportErrorLabel = SettingsRes.strings.settings_support_report_label.desc()
            )
        )
    }
}