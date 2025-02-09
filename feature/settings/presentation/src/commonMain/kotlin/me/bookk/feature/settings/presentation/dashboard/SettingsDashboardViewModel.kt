package me.bookk.feature.settings.presentation.dashboard

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.UpdateColorScheme
import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.dashboard.state.AccountSection
import me.bookk.feature.settings.presentation.dashboard.state.AppearanceSection
import me.bookk.feature.settings.presentation.dashboard.state.AppearanceSection.UIColorScheme
import me.bookk.feature.settings.presentation.dashboard.state.ProfileSection
import me.bookk.feature.settings.presentation.dashboard.state.SettingsState
import me.bookk.feature.settings.presentation.dashboard.state.SupportSection

class SettingsDashboardViewModel(
    private val getSettings: GetSettings,
    private val updateColorScheme: UpdateColorScheme,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState = settingsStateFactory.createSettingsState(createInitData())

    init {
        loadSettings()
    }

    fun onSchemeSelected(scheme: UIColorScheme) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { updateColorScheme.invoke(scheme.toDomain()) },
            onComplete = { uiState.appearance.colorScheme = scheme }
        )
    }

    private fun loadSettings() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getSettings.invoke() },
            onComplete = {
                uiState.appearance.colorScheme = UIColorScheme.from(it.colorScheme)
                uiState.profile.name = it.profile.firstName.desc()
                uiState.profile.lastName = it.profile.lastName.desc()
                uiState.profile.email = it.profile.email.desc()
            }
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