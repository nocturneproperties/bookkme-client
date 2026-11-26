package me.bookk.client.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import library.telegram.TelegramWebApp
import me.bookk.core.config.SiteUrl
import me.bookk.core.presentation.navigation.BackStack
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation
import org.koin.mp.KoinPlatform

@Composable
internal fun rememberSettingsNavigation(
    stack: BackStack<ClientScreen>,
    webApp: TelegramWebApp,
): SettingsNavigation {
    return remember(stack, webApp) {
        val siteUrl = KoinPlatform.getKoin().get<SiteUrl>()
        val openSitePage = { path: String -> webApp.openLink(ClientLink.sitePage(path, siteUrl)) }
        SettingsNavigation(
            onBack = { stack.pop() },
            toBookingProfile = { stack.push(ClientScreen.BookingProfile) },
            toChangeEmail = { stack.push(ClientScreen.ChangeEmail) },
            toNotifications = { stack.push(ClientScreen.NotificationSettings) },
            toPrivacy = { stack.push(ClientScreen.PrivacySettings) },
            toPersonalDataExport = { stack.push(ClientScreen.PersonalDataExport) },
            toDeleteAccount = { stack.push(ClientScreen.DeleteAccount) },
            openContactPage = { openSitePage(ClientLink.SITE_CONTACT_PATH) },
            openTermsOfUse = { openSitePage(ClientLink.SITE_TERMS_PATH) },
            openPrivacyPolicy = { openSitePage(ClientLink.SITE_PRIVACY_PATH) },
        )
    }
}
