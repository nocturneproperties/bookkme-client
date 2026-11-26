package me.bookk.feature.settings.presentation.dashboard

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.designsystem.components.Avatar
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.InitialsAvatarSize
import me.bookk.designsystem.components.MenuCard
import me.bookk.designsystem.components.MenuItem
import me.bookk.designsystem.components.MenuItemTone
import me.bookk.designsystem.components.MenuItemTrailing
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.components.TextButton
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.secondary
import me.bookk.designsystem.uistate.ButtonState
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val SettingsModifier = Modifier
    .fillMaxWidth()
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.5.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.375.cssRem)

@Composable
internal fun SettingsScreen(state: SettingsState) {
    Column(SettingsModifier) {
        ProfileHeader(state.profile)
        MenuCard(accountItems(state.account))
        MenuCard(supportItems(state.support))
        MenuCard(listOf(deleteAccountItem(state.account.deleteAccount)))
    }
}

@Composable
private fun ProfileHeader(profile: ProfileSection) {
    Column(
        Modifier
            .fillMaxWidth()
            .gap(0.25.cssRem)
            .textAlign(TextAlign.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Avatar(
            initials = profile.initials,
            photoUrl = profile.photoUrl,
            modifier = Modifier.margin(bottom = 0.5.cssRem),
            size = InitialsAvatarSize.EXTRA_LARGE,
        )
        Heading(
            profile.fullName,
            HeadingLevel.H1,
            HeadlineLargeTextStyle
                .toModifier()
                .margin(0.px),
        )
        TextBlock(
            SettingsRes.strings.settings_profile_booking_hint.desc(),
            BodyLargeTextStyle
                .toModifier()
                .margin(0.px)
                .secondary(),
        )
        TextButton(profile.edit)
    }
}

private fun accountItems(account: AccountSection): List<MenuItem> {
    return listOf(
        menuItem(
            "notifications",
            account.notifications,
            DesignSystem.images.material_notifications,
        ),
        menuItem(
            id = "privacy",
            button = account.privacy,
            icon = DesignSystem.images.material_shield,
            status = account.privacyStatus,
        ),
        menuItem("export", account.dataExport, DesignSystem.images.material_download),
    )
}

private fun supportItems(support: SupportSection): List<MenuItem> {
    val external = MenuItemTrailing.EXTERNAL
    return listOf(
        menuItem("contact", support.contactPage, DesignSystem.images.material_email, external),
        menuItem("terms", support.terms, DesignSystem.images.material_description, external),
        menuItem("policy", support.policy, DesignSystem.images.material_policy, external),
    )
}

private fun deleteAccountItem(button: ButtonState): MenuItem {
    return menuItem(
        id = "delete",
        button = button,
        icon = DesignSystem.images.material_delete,
        tone = MenuItemTone.DESTRUCTIVE,
    )
}

private fun menuItem(
    id: String,
    button: ButtonState,
    icon: ImageResource,
    trailing: MenuItemTrailing = MenuItemTrailing.CHEVRON,
    tone: MenuItemTone = MenuItemTone.DEFAULT,
    status: StringDesc? = null,
): MenuItem {
    return MenuItem(
        id = id,
        text = button.text,
        icon = icon,
        onClick = { button.onClick?.invoke() },
        status = status,
        trailing = trailing,
        tone = tone,
    )
}
