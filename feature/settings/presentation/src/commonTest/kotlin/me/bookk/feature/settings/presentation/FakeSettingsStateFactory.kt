package me.bookk.feature.settings.presentation

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.test.FakeAppBarState
import me.bookk.designsystem.test.FakeBooleanState
import me.bookk.designsystem.test.FakeButtonState
import me.bookk.designsystem.test.FakeNavigationState
import me.bookk.designsystem.test.FakeNotificationState
import me.bookk.designsystem.test.FakeRefreshState
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.FakeTextState
import me.bookk.feature.settings.presentation.accdelete.DeleteAccountNavigationDestination
import me.bookk.feature.settings.presentation.accdelete.DeleteAccountState
import me.bookk.feature.settings.presentation.contactus.ContactUsNavigationDestination
import me.bookk.feature.settings.presentation.contactus.ContactUsState
import me.bookk.feature.settings.presentation.dashboard.AccountSection
import me.bookk.feature.settings.presentation.dashboard.AppearanceSection
import me.bookk.feature.settings.presentation.dashboard.ProfileSection
import me.bookk.feature.settings.presentation.dashboard.SettingsDashboardDestination
import me.bookk.feature.settings.presentation.dashboard.SettingsState
import me.bookk.feature.settings.presentation.dashboard.SupportSection
import me.bookk.feature.settings.presentation.editprofile.EditProfileNavigationDestination
import me.bookk.feature.settings.presentation.editprofile.EditProfileState
import me.bookk.feature.settings.presentation.notifications.NotificationSettingsDestinations
import me.bookk.feature.settings.presentation.notifications.NotificationSettingsState
import me.bookk.feature.settings.presentation.passkey.PasskeyState

internal class FakeSettingsStateFactory : SettingsStateFactory {
    override fun createSettingsState(initData: SettingsState.InitData): SettingsState = FakeSettingsState()
    override fun createEditProfileState(initData: EditProfileState.InitData): EditProfileState = FakeEditProfileState()
    override fun createContactUsState(initData: ContactUsState.InitData): ContactUsState = FakeContactUsState()
    override fun createDeleteAccountState(initData: DeleteAccountState.InitData): DeleteAccountState = FakeDeleteAccountState()
    override fun createPasskeyState(initData: PasskeyState.InitData): PasskeyState = FakePasskeyState()
    override fun createNotificationSettingsState(): NotificationSettingsState = FakeNotificationSettingsState()
}

internal class FakeSettingsState : SettingsState {
    override val appBar = FakeAppBarState()
    override val appearance = object : AppearanceSection {
        override val title: StringDesc = "".desc()
        override var colorScheme: AppearanceSection.UIColorScheme = AppearanceSection.UIColorScheme.SYSTEM
    }
    override val profile = object : ProfileSection {
        override var name: StringDesc = "".desc()
        override var lastName: StringDesc = "".desc()
        override var email: StringDesc = "".desc()
    }
    override val account = object : AccountSection {
        override val title: StringDesc = "".desc()
        override val notifications = FakeTextState()
        override val passkey = FakeTextState()
        override val logout = FakeTextState()
        override val deleteAccount = FakeTextState()
    }
    override val support = object : SupportSection {
        override val title: StringDesc = "".desc()
        override val contact = FakeTextState()
        override val feature = FakeTextState()
        override val terms = FakeTextState()
        override val policy = FakeTextState()
        override val reportError = FakeTextState()
    }
    override val notification = FakeNotificationState()
    override val navigation = FakeNavigationState<SettingsDashboardDestination>()
}

internal class FakeEditProfileState : EditProfileState {
    override val appBar = FakeAppBarState()
    override val name = FakeTextFieldState()
    override val lastName = FakeTextFieldState()
    override val email = FakeTextFieldState()
    override val confirmButton = FakeButtonState()
    override val notification = FakeNotificationState()
    override val navigation = FakeNavigationState<EditProfileNavigationDestination>()
}

internal class FakeContactUsState : ContactUsState {
    override val appBar = FakeAppBarState()
    override val contactField = FakeTextFieldState()
    override val includeLogsSwitch = FakeBooleanState()
    override val logsExplanationText: StringDesc = "".desc()
    override val submitButton = FakeButtonState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<ContactUsNavigationDestination>()
}

internal class FakeDeleteAccountState : DeleteAccountState {
    override val appBar = FakeAppBarState()
    override val confirmationMessage: StringDesc = "".desc()
    override val confirmation = FakeBooleanState()
    override val deleteButton = FakeButtonState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<DeleteAccountNavigationDestination>()
}

internal class FakePasskeyState : PasskeyState {
    override val appBar = FakeAppBarState()
    override val addPasskeyButton = FakeButtonState()
    override var passkeys: List<PasskeyState.PasskeyItem> = emptyList()
    override val refresh = FakeRefreshState()
    override val notification = FakeNotificationState()

    override fun replacePasskeyList(items: List<PasskeyState.PasskeyItem>) {
        passkeys = items
    }
}

internal class FakeNotificationSettingsState : NotificationSettingsState {
    override val appBar = FakeAppBarState()
    override val appointmentEnabled = FakeBooleanState()
    override val emailEnabled = FakeBooleanState()
    override val pushNotificationsEnabled = FakeBooleanState()
    override val telegramEnabled = FakeBooleanState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<NotificationSettingsDestinations>()
}
