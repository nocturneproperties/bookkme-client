package me.bookk.feature.settings.presentation.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidTextState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.TextState
import me.bookk.feature.settings.presentation.dashboard.state.AccountSection
import me.bookk.feature.settings.presentation.dashboard.state.AppearanceSection
import me.bookk.feature.settings.presentation.dashboard.state.AppearanceSection.UIColorScheme
import me.bookk.feature.settings.presentation.dashboard.state.ProfileSection
import me.bookk.feature.settings.presentation.dashboard.state.SettingsState
import me.bookk.feature.settings.presentation.dashboard.state.SupportSection

internal class AndroidDashboardState(
    initData: SettingsState.InitData
) : SettingsState {
    override val appearance: AppearanceSection = AndroidAppearanceSectionState(initData.appearance)
    override val profile: ProfileSection = AndroidProfileSectionState(initData.profile)
    override val account: AccountSection = AndroidAccountSectionState(initData.account)
    override val support: SupportSection = AndroidSupportState(initData.support)
}

internal class AndroidAppearanceSectionState(
    initData: AppearanceSection.InitData
) : AppearanceSection {
    override val title: StringDesc = initData.title
    override var colorScheme: UIColorScheme by mutableStateOf(UIColorScheme.SYSTEM)
}

internal class AndroidProfileSectionState(
    initData: ProfileSection.InitData
) : ProfileSection {
    override val title: StringDesc = initData.title
    override var name: StringDesc by mutableStateOf("".desc())
    override var lastName: StringDesc by mutableStateOf("".desc())
    override var email: StringDesc by mutableStateOf("".desc())
    override val editProfile: ButtonState = AndroidButtonState(initData.editButtonText)
}

internal class AndroidAccountSectionState(
    initData: AccountSection.InitData
) : AccountSection {
    override val title: StringDesc = initData.title
    override val passkey: TextState = AndroidTextState(initData.passkeyLabel)
    override val logout: TextState = AndroidTextState(initData.logoutLabel)
    override val deleteAccount: TextState = AndroidTextState(initData.deleteAccountLabel)
}

internal class AndroidSupportState(
    initData: SupportSection.InitData
) : SupportSection {
    override val title: StringDesc = initData.title
    override val contact: TextState = AndroidTextState(initData.contactLabel)
    override val feature: TextState = AndroidTextState(initData.featureLabel)
    override val terms: TextState = AndroidTextState(initData.termsLabel)
    override val policy: TextState = AndroidTextState(initData.policyLabel)
    override val reportError: TextState = AndroidTextState(initData.reportErrorLabel)
}