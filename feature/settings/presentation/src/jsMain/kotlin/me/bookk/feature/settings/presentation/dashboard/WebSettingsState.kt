package me.bookk.feature.settings.presentation.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState
import me.bookk.designsystem.uistate.WebTextState
import me.bookk.feature.settings.presentation.dashboard.AppearanceSection.UIColorScheme

internal class WebSettingsState(
    initData: SettingsState.InitData,
) : SettingsState {
    override val appBar = WebAppBarState()
    override val appearance = WebAppearanceSection(initData.appearance)
    override val profile = WebProfileSection()
    override val account = WebAccountSection(initData.account)
    override val support = WebSupportSection(initData.support)
    override val notification = WebNotificationState()
    override val navigation = WebNavigationState<SettingsDashboardDestination>()
}

internal class WebAppearanceSection(
    initData: AppearanceSection.InitData,
) : AppearanceSection {
    override val title: StringDesc = initData.title
    override var colorScheme: UIColorScheme by mutableStateOf(UIColorScheme.SYSTEM)
}

internal class WebProfileSection : ProfileSection {
    override var email: StringDesc by mutableStateOf("".desc())
    override var fullName: StringDesc by mutableStateOf("".desc())
    override var initials: String by mutableStateOf("")
    override val edit = WebButtonState()
}

internal class WebAccountSection(
    initData: AccountSection.InitData,
) : AccountSection {
    override val dataExport = WebButtonState()
    override val changeEmail = WebTextState()
    override val privacy = WebButtonState()
    override var privacyStatus: StringDesc? by mutableStateOf(null)
    override val title: StringDesc = initData.title
    override val notifications = WebButtonState(text = initData.notificationsLabel)
    override val passkey = WebTextState(initData.passkeyLabel)
    override val logout = WebTextState(initData.logoutLabel)
    override val deleteAccount = WebButtonState(text = initData.deleteAccountLabel)
}

internal class WebSupportSection(
    initData: SupportSection.InitData,
) : SupportSection {
    override val title: StringDesc = initData.title
    override val contact = WebTextState(initData.contactLabel)
    override val contactPage = WebButtonState(text = initData.contactLabel)
    override val feature = WebTextState(initData.featureLabel)
    override val terms = WebButtonState(text = initData.termsLabel)
    override val policy = WebButtonState(text = initData.policyLabel)
    override val reportError = WebTextState(initData.reportErrorLabel)
    override var onContactClick: () -> Unit = {}
}
