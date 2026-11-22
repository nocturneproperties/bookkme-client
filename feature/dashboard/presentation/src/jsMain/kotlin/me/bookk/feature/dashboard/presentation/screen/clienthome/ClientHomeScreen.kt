package me.bookk.feature.dashboard.presentation.screen.clienthome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.dvh
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flex
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.android.feature.dashboard.resources.DashboardRes
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.simple.BannerErrorState
import me.bookk.site.designsystem.components.AppCard
import me.bookk.site.designsystem.components.BannerErrorView
import me.bookk.site.designsystem.components.CardButton
import me.bookk.site.designsystem.components.DateTile
import me.bookk.site.designsystem.components.EmptyStateView
import me.bookk.site.designsystem.components.FittingList
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.HorizontalDivider
import me.bookk.site.designsystem.components.IconButton
import me.bookk.site.designsystem.components.IconTile
import me.bookk.site.designsystem.components.InitialLoadingView
import me.bookk.site.designsystem.components.ListRowButton
import me.bookk.site.designsystem.components.NumberedSteps
import me.bookk.site.designsystem.components.ObserveNavigation
import me.bookk.site.designsystem.components.StatusPill
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextButton
import me.bookk.site.designsystem.components.TintedIcon
import me.bookk.site.designsystem.components.fittingItem
import me.bookk.site.designsystem.resources.WebDesignSystem
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.BodySmallTextStyle
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.site.designsystem.theme.TitleMediumTextStyle
import me.bookk.site.designsystem.theme.secondary
import me.bookk.site.designsystem.theme.themed
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.koin.core.parameter.parametersOf

private val BookAgainMinHeight = 7.cssRem

private val ClientHomeModifier = Modifier
    .fillMaxWidth()
    .maxWidth(40.cssRem)
    .height(100.dvh)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.5.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.5.cssRem)

private val SectionModifier = Modifier
    .fillMaxWidth()
    .minHeight(0.px)
    .gap(0.5.cssRem)

@Composable
fun ClientHomeScreen(webApp: TelegramWebApp) {
    val startParameter = webApp.initDataUnsafe.startParam.orEmpty()
    val state = rememberViewModel<ClientHomeViewModel> { parametersOf(startParameter) }.uiState
    ObserveTelegramNotifications(webApp, state.notifications)
    ObserveNavigation(state.navigation) { destination ->
        when (destination) {
            ClientHomeDestinations.Settings,
            ClientHomeDestinations.Bookings,
            ClientHomeDestinations.Businesses,
            is ClientHomeDestinations.BookingDetails,
            is ClientHomeDestinations.Services,
            -> Unit
        }
    }
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
            TintedIcon(WebDesignSystem.images.material_settings, ColorToken.PrimaryText)
        }
    }
}

@Composable
private fun UpcomingSection(state: ClientHomeState) {
    Column(SectionModifier) {
        SectionHeader(
            DashboardRes.strings.dashboard_client_home_upcoming.desc(),
            state.allBookingsButton,
        )
        val emptyState = state.upcoming.emptyState
        if (state.hasNoUpcomingBookings && emptyState != null) EmptyStateView(emptyState)
        ListContent(state.upcoming) { bookings ->
            FittingList(bookings, Modifier.gap(0.5.cssRem)) { visibleBookings ->
                visibleBookings.forEach { UpcomingBookingCard(it) }
            }
        }
    }
}

@Composable
private fun UpcomingBookingCard(item: UpcomingBookingItem) {
    CardButton(onClick = item.onClick, modifier = Modifier.fittingItem()) {
        DateTile(item.weekday, item.day)
        Column(Modifier.gap(0.125.cssRem)) {
            TextBlock(
                item.businessName.desc(),
                TitleMediumTextStyle
                    .toModifier()
                    .margin(0.px),
            )
            SecondaryLine(item.services.desc())
            SecondaryLine(item.timeAndSpecialist)
            StatusPill(item.status, item.statusColor, Modifier.margin(top = 0.375.cssRem))
        }
    }
}

@Composable
private fun BookAgainSection(state: ClientHomeState) {
    if (!state.showsBookAgain) return
    Column(
        SectionModifier
            .flex(1, 1, 0.px)
            .minHeight(BookAgainMinHeight),
    ) {
        SectionHeader(
            DashboardRes.strings.dashboard_client_home_book_again.desc(),
            state.allBusinessesButton,
        )
        ListContent(state.businesses) { businesses ->
            FittingList(businesses, Modifier.flexGrow(1)) { visibleBusinesses ->
                SavedBusinessesCard(visibleBusinesses)
            }
        }
    }
}

@Composable
private fun SavedBusinessesCard(businesses: List<SavedBusinessItem>) {
    if (businesses.isEmpty()) return
    AppCard(
        Modifier
            .fillMaxWidth()
            .padding(0.px)
            .gap(0.px),
    ) {
        businesses.forEachIndexed { index, business ->
            SavedBusinessRow(business, isLast = index == businesses.lastIndex)
        }
    }
}

@Composable
private fun SavedBusinessRow(item: SavedBusinessItem, isLast: Boolean) {
    ListRowButton(onClick = item.onClick, modifier = Modifier.fittingItem()) {
        IconTile(WebDesignSystem.images.material_business)
        Column(Modifier.fillMaxWidth()) {
            TextBlock(
                item.name.desc(),
                BodyLargeTextStyle
                    .toModifier()
                    .fontWeight(FontWeight.Medium)
                    .margin(0.px),
            )
            TextBlock(
                item.subtitle,
                BodySmallTextStyle
                    .toModifier()
                    .margin(0.px)
                    .secondary(),
            )
        }
    }
    if (!isLast) HorizontalDivider()
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
            BodyLargeTextStyle
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
private fun <T> ListContent(state: ListState<T>, content: @Composable (List<T>) -> Unit) {
    val errorState = state.errorState
    if (errorState != null) {
        InitialLoadError(errorState.subtitle, errorState.onRetryClick)
        return
    }
    state.bannerError?.let { BannerErrorView(it) }
    content(state.items)
}

@Composable
private fun InitialLoadError(message: StringDesc, onRetry: () -> Unit) {
    val banner =
        remember(message, onRetry) { BannerErrorState(text = message, onRetryClick = onRetry) }
    BannerErrorView(banner)
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
private fun SecondaryLine(text: StringDesc) {
    TextBlock(
        text,
        BodyMediumTextStyle
            .toModifier()
            .margin(0.px)
            .secondary(),
    )
}
