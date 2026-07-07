package me.bookk.feature.settings.presentation.dashboard

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextState
import me.bookk.feature.settings.domain.api.entity.ColorScheme

interface SettingsState {
    val appBar: AppBarState
    val appearance: AppearanceSection
    val profile: ProfileSection
    val account: AccountSection
    val support: SupportSection

    val notification: PresentationNotificationState
    val navigation: NavigationState<SettingsDashboardDestination>

    class InitData(
        val appearance: AppearanceSection.InitData,
        val account: AccountSection.InitData,
        val support: SupportSection.InitData
    )
}

interface AppearanceSection {
    val title: StringDesc
    var colorScheme: UIColorScheme

    class InitData(
        val title: StringDesc
    )

    //UI model to prevent need of Settings Domain API module to be exported to objc header
    enum class UIColorScheme(val title: StringDesc) {
        DARK(SettingsRes.strings.settings_theme_mode_dark.desc()),
        LIGHT(SettingsRes.strings.settings_theme_mode_light.desc()),
        SYSTEM(SettingsRes.strings.settings_theme_mode_system.desc());

        internal fun toDomain(): ColorScheme {
            return when (this) {
                DARK -> ColorScheme.DARK
                LIGHT -> ColorScheme.LIGHT
                SYSTEM -> ColorScheme.SYSTEM
            }
        }

        companion object {
            internal fun from(scheme: ColorScheme): UIColorScheme {
                return when (scheme) {
                    ColorScheme.DARK -> DARK
                    ColorScheme.LIGHT -> LIGHT
                    ColorScheme.SYSTEM -> SYSTEM
                }
            }
        }
    }
}

interface ProfileSection {
    var name: StringDesc
    var lastName: StringDesc
    var email: StringDesc

}

interface AccountSection {
    val title: StringDesc
    val notifications: TextState
    val passkey: TextState
    val logout: TextState
    val deleteAccount: TextState

    class InitData(
        val title: StringDesc,
        val notificationsLabel: StringDesc,
        val passkeyLabel: StringDesc,
        val logoutLabel: StringDesc,
        val deleteAccountLabel: StringDesc
    )
}

interface SupportSection {
    val title: StringDesc
    val contact: TextState
    val feature: TextState
    val terms: TextState
    val policy: TextState
    val reportError: TextState

    class InitData(
        val title: StringDesc,
        val contactLabel: StringDesc,
        val featureLabel: StringDesc,
        val termsLabel: StringDesc,
        val policyLabel: StringDesc,
        val reportErrorLabel: StringDesc
    )
}