package me.bookk.feature.dashboard.presentation.screen.clienthome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.dashboard.resources.DashboardRes
import me.bookk.designsystem.components.BannerErrorView
import me.bookk.designsystem.components.CardButton
import me.bookk.designsystem.components.CardRail
import me.bookk.designsystem.components.CardRailItemStyle
import me.bookk.designsystem.components.EmptyStateView
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.IconButton
import me.bookk.designsystem.components.InitialLoadingView
import me.bookk.designsystem.components.InitialsAvatar
import me.bookk.designsystem.components.NumberedSteps
import me.bookk.designsystem.components.StatusPill
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.components.TextButton
import me.bookk.designsystem.components.TintedIcon
import me.bookk.designsystem.components.fillScreenHeight
import me.bookk.designsystem.components.fixedLines
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.designsystem.theme.LeadTextStyle
import me.bookk.designsystem.theme.TitleMediumTextStyle
import me.bookk.designsystem.theme.active
import me.bookk.designsystem.theme.secondary
import me.bookk.designsystem.theme.themed
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.simple.BannerErrorState
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private const val TIME_LINES = 2
private const val TIME_LINE_HEIGHT = 1.5

private val UpcomingBookingCardWidth = 20.cssRem
private val SavedBusinessCardWidth = 12.cssRem

private val ClientHomeModifier = Modifier
    .fillMaxWidth()
    .maxWidth(40.cssRem)
    .fillScreenHeight()
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.5.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.5.cssRem)

private val SectionModifier = Modifier
    .fillMaxWidth()
    .minHeight(0.px)
    .gap(0.5.cssRem)

@Composable
internal fun ClientHomeScreen(state: ClientHomeState) {
    Column(ClientHomeModifier) {
        if (state.isLoading) {
            InitialLoadingView(Modifier.flexGrow(1))
        } else {
            LoadedContent(state)
        }
    }
}

@Composable
private fun LoadedContent(state: ClientHomeState) {
    state.greeting?.let { Greeting(it, state.settingsButton) }
    if (state.hasNoBusiness) {
        NoBusinessYet()
    } else {
        UpcomingSection(state)
        BookAgainSection(state)
    }
}

@Composable
private fun Greeting(greeting: StringDesc, settingsButton: ButtonState) {
    Row(
        Modifier
            .fillMaxWidth()
            .gap(0.75.cssRem),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Heading(
            greeting,
            HeadingLevel.H1,
            HeadlineLargeTextStyle
                .toModifier()
                .margin(0.px),
        )
        Spacer()
        IconButton(
            onClick = { settingsButton.onClick?.invoke() },
            description = settingsButton.text,
            modifier = Modifier
                .size(2.75.cssRem)
                .minWidth(2.75.cssRem)
                .backgroundColor(ColorToken.Elevated.themed),
        ) {
            TintedIcon(DesignSystem.images.material_settings, ColorToken.PrimaryText)
        }
    }
}

@Composable
private fun NoBusinessYet() {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 1.5.cssRem, leftRight = 0.5.cssRem)
            .gap(1.cssRem)
            .textAlign(TextAlign.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Heading(
            DashboardRes.strings.dashboard_client_home_empty_title.desc(),
            HeadingLevel.H2,
            HeadlineSmallTextStyle
                .toModifier()
                .margin(0.px),
        )
        TextBlock(
            DashboardRes.strings.dashboard_client_home_empty_body.desc(),
            LeadTextStyle
                .toModifier()
                .maxWidth(20.cssRem)
                .secondary(),
        )
        NumberedSteps(
            listOf(
                DashboardRes.strings.dashboard_client_home_empty_step_link.desc(),
                DashboardRes.strings.dashboard_client_home_empty_step_pick.desc(),
                DashboardRes.strings.dashboard_client_home_empty_step_confirm.desc(),
            ),
            Modifier.margin(top = 1.cssRem),
        )
    }
}

@Composable
private fun UpcomingSection(state: ClientHomeState) {
    Column(SectionModifier.flexShrink(0)) {
        SectionHeader(
            DashboardRes.strings.dashboard_client_home_upcoming.desc(),
            state.allBookingsButton,
        )
        val emptyState = state.upcoming.emptyState
        if (state.hasNoUpcomingBookings && emptyState != null) EmptyStateView(emptyState)
        ListContent(state.upcoming) { bookings ->
            CardRail(snapsToEachCard = true) {
                bookings.forEach { UpcomingBookingCard(it) }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: StringDesc, button: ButtonState) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        SectionTitle(title)
        Spacer()
        TextButton(button)
    }
}

@Composable
private fun SectionTitle(text: StringDesc) {
    Heading(
        text,
        HeadingLevel.H2,
        TitleMediumTextStyle
            .toModifier()
            .margin(0.px),
    )
}

@Composable
private fun <T> ListContent(state: ListState<T>, content: @Composable (List<T>) -> Unit) {
    val errorState = state.errorState
    if (errorState != null) {
        InitialLoadError(errorState.subtitle, errorState.onRetryClick)
    } else {
        state.bannerError?.let { BannerErrorView(it) }
        content(state.items)
    }
}

@Composable
private fun InitialLoadError(message: StringDesc, onRetry: () -> Unit) {
    val banner =
        remember(message, onRetry) { BannerErrorState(text = message, onRetryClick = onRetry) }
    BannerErrorView(banner)
}

@Composable
private fun UpcomingBookingCard(item: UpcomingBookingItem) {
    CardButton(
        onClick = item.onClick,
        modifier = CardRailItemStyle
            .toModifier()
            .width(UpcomingBookingCardWidth)
            .padding(1.25.cssRem),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .gap(1.cssRem),
        ) {
            StatusPill(item.status, item.statusColor)
            Column(Modifier.gap(0.5.cssRem)) {
                UpcomingBookingWhen(item)
                UpcomingBookingWhere(item)
            }
        }
    }
}

@Composable
private fun UpcomingBookingWhen(item: UpcomingBookingItem) {
    Column {
        TextBlock(
            item.date.desc(),
            HeadlineSmallTextStyle
                .toModifier()
                .fontSize(1.75.cssRem)
                .fontWeight(FontWeight.Bold)
                .margin(0.px),
        )
        UpcomingBookingTime(item.time)
    }
}

@Composable
private fun UpcomingBookingWhere(item: UpcomingBookingItem) {
    Column(Modifier.gap(0.1.cssRem)) {
        TextBlock(
            item.businessName.desc(),
            TitleMediumTextStyle
                .toModifier()
                .fontWeight(FontWeight.SemiBold)
                .margin(0.px),
        )
        TextBlock(
            item.services.desc(),
            BodyMediumTextStyle
                .toModifier()
                .margin(0.px)
                .secondary(),
        )
    }
}

@Composable
private fun UpcomingBookingTime(text: StringDesc) {
    TextBlock(
        text,
        BodyLargeTextStyle
            .toModifier()
            .margin(0.px)
            .fixedLines(TIME_LINES, TIME_LINE_HEIGHT)
            .secondary(),
    )
}

@Composable
private fun BookAgainSection(state: ClientHomeState) {
    if (state.showsBookAgain) {
        Column(SectionModifier.flexShrink(0)) {
            SectionTitle(DashboardRes.strings.dashboard_client_home_book_again.desc())
            ListContent(state.businesses) { businesses ->
                CardRail {
                    businesses.forEach { SavedBusinessCard(it) }
                }
            }
        }
    }
}

@Composable
private fun SavedBusinessCard(item: SavedBusinessItem) {
    CardButton(
        onClick = item.onClick,
        modifier = CardRailItemStyle
            .toModifier()
            .width(SavedBusinessCardWidth)
            .padding(1.125.cssRem),
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .gap(0.875.cssRem),
        ) {
            InitialsAvatar(
                item.initials,
                Modifier
                    .size(3.25.cssRem)
                    .borderRadius(0.875.cssRem),
            )
            SavedBusinessText(item)
            BookAction()
        }
    }
}

@Composable
private fun SavedBusinessText(item: SavedBusinessItem) {
    Column(Modifier.gap(0.125.cssRem)) {
        TextBlock(
            item.name.desc(),
            LeadTextStyle
                .toModifier()
                .fontWeight(FontWeight.SemiBold)
                .margin(0.px),
        )
        TextBlock(
            item.subtitle,
            BodyMediumTextStyle
                .toModifier()
                .margin(0.px)
                .secondary(),
        )
    }
}

@Composable
private fun BookAction() {
    Row(Modifier.gap(0.125.cssRem), verticalAlignment = Alignment.CenterVertically) {
        TextBlock(
            DashboardRes.strings.dashboard_client_home_book.desc(),
            BodyLargeTextStyle
                .toModifier()
                .fontWeight(FontWeight.SemiBold)
                .margin(0.px)
                .active(),
        )
        TintedIcon(
            DesignSystem.images.material_chevron_right,
            ColorToken.ActionText,
            Modifier.size(1.cssRem),
        )
    }
}
