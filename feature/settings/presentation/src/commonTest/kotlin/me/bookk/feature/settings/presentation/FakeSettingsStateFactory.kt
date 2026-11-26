package me.bookk.feature.settings.presentation

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
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
import me.bookk.feature.settings.presentation.confirmation.ChatConfirmationState
import me.bookk.feature.settings.presentation.dashboard.AccountSection
import me.bookk.feature.settings.presentation.dashboard.AppearanceSection
import me.bookk.feature.settings.presentation.dashboard.ProfileSection
import me.bookk.feature.settings.presentation.dashboard.SettingsDashboardDestination
import me.bookk.feature.settings.presentation.dashboard.SettingsState
import me.bookk.feature.settings.presentation.dashboard.SupportSection
import me.bookk.feature.settings.presentation.editprofile.EditProfileNavigationDestination
import me.bookk.feature.settings.presentation.editprofile.EditProfileState
import me.bookk.feature.settings.presentation.export.PersonalDataExportDestinations
import me.bookk.feature.settings.presentation.export.PersonalDataExportState
import me.bookk.feature.settings.presentation.export.UIExportStage
import me.bookk.feature.settings.presentation.export.UIExportStatus
import me.bookk.feature.settings.presentation.notifications.NotificationSettingsDestinations
import me.bookk.feature.settings.presentation.notifications.NotificationSettingsState
import me.bookk.feature.settings.presentation.passkey.PasskeyState
import me.bookk.feature.settings.presentation.privacy.LegalDocumentItem
import me.bookk.feature.settings.presentation.privacy.LegalDocumentsSection
import me.bookk.feature.settings.presentation.privacy.PrivacySettingsDestinations
import me.bookk.feature.settings.presentation.privacy.PrivacySettingsState

internal class FakeSettingsStateFactory : SettingsStateFactory {
    override fun createPersonalDataExportState(): PersonalDataExportState {
        return FakePersonalDataExportState()
    }

    override fun createSettingsState(initData: SettingsState.InitData): SettingsState =
        FakeSettingsState()

    override fun createEditProfileState(): EditProfileState {
        return FakeEditProfileState()
    }

    override fun createDeleteAccountState(): DeleteAccountState {
        return FakeDeleteAccountState()
    }

    override fun createPasskeyState(initData: PasskeyState.InitData): PasskeyState =
        FakePasskeyState()

    override fun createNotificationSettingsState(): NotificationSettingsState =
        FakeNotificationSettingsState()

    override fun createPrivacySettingsState(): PrivacySettingsState {
        return FakePrivacySettingsState()
    }
}

internal class FakeSettingsState : SettingsState {
    override val appBar = FakeAppBarState()
    override val appearance = object : AppearanceSection {
        override val title: StringDesc = "".desc()
        override var colorScheme: AppearanceSection.UIColorScheme =
            AppearanceSection.UIColorScheme.SYSTEM
    }
    override val profile = object : ProfileSection {
        override var email: StringDesc = "".desc()
        override var fullName: StringDesc = "".desc()
        override var initials: String = ""
        override var photoUrl: String? = null
        override val edit = FakeButtonState()
    }
    override val account = object : AccountSection {
        override val dataExport = FakeButtonState()
        override val changeEmail = FakeTextState()
        override val privacy = FakeButtonState()
        override var privacyStatus: StringDesc? = null
        override val title: StringDesc = "".desc()
        override val notifications = FakeButtonState()
        override val passkey = FakeTextState()
        override val logout = FakeTextState()
        override val deleteAccount = FakeButtonState()
    }
    override val support = object : SupportSection {
        override val title: StringDesc = "".desc()
        override val contact = FakeTextState()
        override val contactPage = FakeButtonState()
        override val feature = FakeTextState()
        override val terms = FakeButtonState()
        override val policy = FakeButtonState()
        override val reportError = FakeTextState()
        override var onContactClick: () -> Unit = {}
    }
    override val notification = FakeNotificationState()
    override val navigation = FakeNavigationState<SettingsDashboardDestination>()
}

internal class FakeEditProfileState : EditProfileState {
    override val appBar = FakeAppBarState()
    override val name = FakeTextFieldState()
    override val phone = FakeTextFieldState()
    override val email = FakeTextFieldState()
    override val changeEmail = FakeButtonState()
    override val confirmButton = FakeButtonState()
    override val notification = FakeNotificationState()
    override val navigation = FakeNavigationState<EditProfileNavigationDestination>()
}

internal class FakeDeleteAccountState : DeleteAccountState {
    override val appBar = FakeAppBarState()
    override var confirmationMessage: StringDesc = "".desc()
    override var pendingDeletion: StringDesc? = null
    override val confirmation = FakeBooleanState()
    override val deleteButton = FakeButtonState()
    override val chatConfirmation = FakeChatConfirmationState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<DeleteAccountNavigationDestination>()
}

internal class FakeChatConfirmationState : ChatConfirmationState {
    override var notice: StringDesc? = null
    override var isWriteAccessRequested: Boolean = false
    override var onWriteAccessAnswered: (Boolean) -> Unit = {}
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

internal class FakePrivacySettingsState : PrivacySettingsState {
    override val appBar = FakeAppBarState()
    override var diagnosticsTitle: StringDesc = "".desc()
    override val crashReporting = FakeBooleanState()
    override var crashReportingDescription: StringDesc = "".desc()
    override val legal = FakeLegalDocumentsSectionState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<PrivacySettingsDestinations>()
}

internal class FakeLegalDocumentsSectionState : LegalDocumentsSection {
    override var title: StringDesc = "".desc()
    override var documents: List<LegalDocumentItem> = emptyList()
    override var isLoading: Boolean = false
    override var message: StringDesc? = null
    override var notice: StringDesc? = null
    override val accept = FakeButtonState()
    override val retry = FakeButtonState()
}

internal class FakePersonalDataExportState : PersonalDataExportState {
    override val appBar = FakeAppBarState()
    override var status = UIExportStatus(
        title = "".desc(),
        message = null,
        footer = null,
        icon = null,
        color = ColorToken.ActionText,
        isInProgress = false,
        showsFile = false,
        stage = UIExportStage.REQUEST,
    )
    override var expiry: StringDesc? = null
    override var requestAvailability: StringDesc? = null
    override var error: StringDesc? = null
    override val request = FakeButtonState()
    override val chatConfirmation = FakeChatConfirmationState()
    override val download = FakeButtonState()
    override val retry = FakeButtonState()
    override val notifications = FakeNotificationState()
    override val navigation = FakeNavigationState<PersonalDataExportDestinations>()
}
