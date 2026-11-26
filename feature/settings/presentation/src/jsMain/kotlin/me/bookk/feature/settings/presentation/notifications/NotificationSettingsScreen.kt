package me.bookk.feature.settings.presentation.notifications

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc
import library.telegram.TelegramBackButton
import library.telegram.TelegramWebApp
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.SwitchCard
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.secondary
import me.bookk.designsystem.uistate.BooleanState
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val NotificationSettingsModifier = Modifier
    .fillMaxWidth()
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.5.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.375.cssRem)

@Composable
internal fun NotificationSettingsScreen(state: NotificationSettingsState, webApp: TelegramWebApp) {
    TelegramBackButton(webApp, state.appBar.onBackClick)
    Column(NotificationSettingsModifier) {
        Heading(
            SettingsRes.strings.settings_notifications_heading.desc(),
            HeadingLevel.H1,
            HeadlineLargeTextStyle
                .toModifier()
                .fontWeight(FontWeight.ExtraBold)
                .margin(0.px),
        )
        SwitchSection(
            header = SettingsRes.strings.settings_notifications_types_header,
            footer = SettingsRes.strings.settings_notifications_types_footer,
            switches = listOf(state.appointmentEnabled),
        )
        SwitchSection(
            header = SettingsRes.strings.settings_notifications_channels_header,
            footer = SettingsRes.strings.settings_notifications_channels_footer,
            switches = listOf(state.telegramEnabled, state.emailEnabled),
        )
    }
}

@Composable
private fun SwitchSection(
    header: StringResource,
    footer: StringResource,
    switches: List<BooleanState>,
) {
    if (switches.any { it.isVisible }) {
        Column(
            Modifier
                .fillMaxWidth()
                .gap(0.5.cssRem),
        ) {
            Header(header.desc(), Modifier.margin(left = 0.25.cssRem, top = 0.px, bottom = 0.px))
            SwitchCard(switches)
            TextBlock(
                footer.desc(),
                BodyMediumTextStyle
                    .toModifier()
                    .margin(leftRight = 1.cssRem, topBottom = 0.px)
                    .secondary(),
            )
        }
    }
}
