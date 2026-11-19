package me.bookk.site.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextOverflow
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.RowScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexBasis
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.textOverflow
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.content.LandingContent
import me.bookk.site.content.TimeSlot
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TintedIcon
import me.bookk.site.designsystem.theme.BodySmallTextStyle
import me.bookk.site.designsystem.theme.LabelMediumTextStyle
import me.bookk.site.designsystem.theme.LabelSmallTextStyle
import me.bookk.site.designsystem.theme.SurfaceInsetColor
import me.bookk.site.designsystem.theme.active
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.secondary
import me.bookk.site.designsystem.theme.webColors
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val ShareLinkSnippetStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .gap(0.625.cssRem)
        .padding(topBottom = 0.75.cssRem, leftRight = 0.875.cssRem)
        .borderRadius(0.75.cssRem)
        .border(1.px, LineStyle.Dashed, ColorToken.Divider.resolve(colorMode.webColors()))
}

val TimeSlotStyle = CssStyle.base {
    Modifier
        .flexGrow(1)
        .flexBasis(0.px)
        .padding(topBottom = 0.625.cssRem)
        .borderRadius(0.625.cssRem)
        .textAlign(TextAlign.Center)
        .border(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colorMode.webColors()))
}

val SelectedTimeSlotStyle = CssStyle.base {
    Modifier
        .border(2.px, LineStyle.Solid, ColorToken.ActionText.resolve(colorMode.webColors()))
        .fontWeight(FontWeight.SemiBold)
}

val RequestSnippetStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .gap(0.5.cssRem)
        .padding(topBottom = 0.625.cssRem, leftRight = 0.75.cssRem)
        .borderRadius(0.75.cssRem)
        .backgroundColor(SurfaceInsetColor.value())
}

val RequestActionStyle = CssStyle.base {
    Modifier
        .flexShrink(0)
        .padding(topBottom = 0.375.cssRem, leftRight = 0.75.cssRem)
        .borderRadius(9999.px)
        .fontWeight(FontWeight.SemiBold)
}

val ApproveRequestActionStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .backgroundColor(ColorToken.ButtonActive.resolve(colors))
        .color(ColorToken.OnAction.resolve(colors))
}

@Composable
fun ShareLinkSnippet() {
    Row(
        ShareLinkSnippetStyle
            .toModifier()
            .then(decorative()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TintedIcon(SiteRes.images.material_link, ColorToken.PrimaryText, Modifier.size(1.cssRem))
        TextBlock(
            SiteRes.strings.site_step_sample_link.desc(),
            BodySmallTextStyle
                .toModifier()
                .flexGrow(1)
                .minWidth(0.px)
                .whiteSpace(WhiteSpace.NoWrap)
                .overflow(Overflow.Hidden)
                .textOverflow(TextOverflow.Ellipsis),
        )
        TextBlock(
            SiteRes.strings.site_step_copy.desc(),
            LabelMediumTextStyle
                .toModifier()
                .fontWeight(FontWeight.SemiBold)
                .active(),
        )
    }
}

@Composable
fun TimeSlotsSnippet() {
    Row(
        Modifier
            .fillMaxWidth()
            .gap(0.5.cssRem)
            .then(decorative()),
    ) {
        LandingContent.sampleTimeSlots.forEach { TimeSlotChip(it) }
    }
}

@Composable
private fun RowScope.TimeSlotChip(slot: TimeSlot) {
    val selected = if (slot.isSelected) SelectedTimeSlotStyle.toModifier() else Modifier
    TextBlock(
        slot.label().desc(),
        TimeSlotStyle
            .toModifier()
            .then(BodySmallTextStyle.toModifier())
            .then(selected),
    )
}

@Composable
fun ApproveRequestSnippet() {
    Row(
        RequestSnippetStyle
            .toModifier()
            .then(decorative()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier
                .flexGrow(1)
                .minWidth(0.px)
                .overflow(Overflow.Hidden),
        ) {
            TextBlock(
                SiteRes.strings.site_step_sample_client.desc(),
                BodySmallTextStyle
                    .toModifier()
                    .fontWeight(FontWeight.SemiBold)
                    .whiteSpace(WhiteSpace.NoWrap)
                    .textOverflow(TextOverflow.Ellipsis)
                    .overflow(Overflow.Hidden),
            )
            TextBlock(
                SiteRes.strings.site_step_sample_time.desc(),
                LabelSmallTextStyle
                    .toModifier()
                    .whiteSpace(WhiteSpace.NoWrap)
                    .textOverflow(TextOverflow.Ellipsis)
                    .overflow(Overflow.Hidden)
                    .secondary(),
            )
        }
        RequestAction(DesignSystem.strings.action_decline.desc(), isPrimary = false)
        RequestAction(DesignSystem.strings.action_approve.desc(), isPrimary = true)
    }
}

@Composable
private fun RequestAction(text: StringDesc, isPrimary: Boolean) {
    val style = if (isPrimary) ApproveRequestActionStyle.toModifier() else Modifier.secondary()
    TextBlock(
        text,
        RequestActionStyle
            .toModifier()
            .then(LabelMediumTextStyle.toModifier())
            .then(style),
    )
}

private fun decorative(): Modifier {
    return Modifier.attrsModifier { attr("aria-hidden", "true") }
}
