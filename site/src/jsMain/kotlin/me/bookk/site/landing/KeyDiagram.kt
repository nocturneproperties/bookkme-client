package me.bookk.site.landing

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexBasis
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.letterSpacing
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TintedIcon
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.BodySmallTextStyle
import me.bookk.site.designsystem.theme.SurfaceCardColor
import me.bookk.site.designsystem.theme.SurfaceInsetColor
import me.bookk.site.designsystem.theme.TitleMediumTextStyle
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.secondary
import me.bookk.site.designsystem.theme.webColors
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private const val ERASED_FILL_PERCENT = 50

class KeyField(
    val label: StringDesc,
    val ciphertext: StringDesc,
)

class KeyHolder(
    val name: StringDesc,
    val isDeleted: Boolean,
)

val KeyDiagramStyle = CssStyle.base {
    Modifier
        .gap(0.875.cssRem)
        .padding(2.cssRem)
        .borderRadius(1.5.cssRem)
        .backgroundColor(SurfaceCardColor.value())
}

val KeyHolderChipStyle = CssStyle.base {
    Modifier
        .width(8.cssRem)
        .flexShrink(0)
        .padding(topBottom = 0.625.cssRem, leftRight = 0.875.cssRem)
        .borderRadius(0.75.cssRem)
        .backgroundColor(SurfaceInsetColor.value())
}

val DeletedKeyHolderChipStyle = CssStyle.base {
    Modifier
        .backgroundColor(Colors.Transparent)
        .border(1.px, LineStyle.Dashed, ColorToken.Divider.resolve(colorMode.webColors()))
}

val KeyRecordStyle = CssStyle.base {
    Modifier
        .flexGrow(1)
        .flexShrink(1)
        .flexBasis(12.cssRem)
        .gap(0.625.cssRem)
        .padding(topBottom = 0.625.cssRem, leftRight = 0.875.cssRem)
        .borderRadius(0.75.cssRem)
        .border(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colorMode.webColors()))
}

val ErasedKeyRecordStyle = CssStyle.base {
    Modifier
        .styleModifier {
            property(
                "background-color",
                "color-mix(in srgb, ${SurfaceInsetColor.value()} $ERASED_FILL_PERCENT%, transparent)",
            )
        }
        .border(1.px, LineStyle.Dashed, ColorToken.Divider.resolve(colorMode.webColors()))
}

val CiphertextStyle = CssStyle.base {
    Modifier
        .fontFamily("ui-monospace", "SFMono-Regular", "Menlo", "Consolas", "monospace")
        .letterSpacing(0.04.em)
}

val KeyFieldChipStyle = CssStyle.base {
    Modifier.width(9.5.cssRem)
}

@Composable
fun TeamKeyDiagram(holders: List<KeyHolder>, modifier: Modifier = Modifier) {
    KeyDiagramFrame(
        SiteRes.strings.site_privacy_encryption_title.desc(),
        SiteRes.strings.site_privacy_encryption_description.desc(),
        modifier,
    ) {
        holders.forEach { KeyHolderRow(it) }
    }
}

@Composable
fun PersonalKeyDiagram(fields: List<KeyField>, modifier: Modifier = Modifier) {
    KeyDiagramFrame(
        SiteRes.strings.site_clients_keys_title.desc(),
        SiteRes.strings.site_clients_keys_description.desc(),
        modifier,
    ) {
        fields.forEach { KeyFieldRow(it) }
    }
}

@Composable
private fun KeyDiagramFrame(
    title: StringDesc,
    description: StringDesc,
    modifier: Modifier,
    rows: @Composable () -> Unit,
) {
    Column(
        KeyDiagramStyle
            .toModifier()
            .then(modifier),
    ) {
        Column(
            Modifier
                .gap(0.375.cssRem)
                .margin(bottom = 0.75.cssRem),
        ) {
            Heading(title, HeadingLevel.H3, TitleMediumTextStyle.toModifier())
            TextBlock(
                description,
                BodyMediumTextStyle
                    .toModifier()
                    .secondary(),
            )
        }
        rows()
    }
}

@Composable
private fun KeyRow(content: @Composable () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .gap(0.75.cssRem)
            .flexWrap(FlexWrap.Wrap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

@Composable
private fun KeyFieldRow(field: KeyField) {
    KeyRow {
        TextBlock(
            field.label,
            KeyHolderChipStyle
                .toModifier()
                .then(KeyFieldChipStyle.toModifier())
                .then(BodySmallTextStyle.toModifier())
                .fontWeight(FontWeight.SemiBold),
        )
        KeyIcon(isRevoked = false)
        Row(KeyRecordStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
            TintedIcon(
                SiteRes.images.material_lock,
                ColorToken.SecondaryText,
                Modifier.size(1.cssRem),
            )
            TextBlock(
                field.ciphertext,
                CiphertextStyle
                    .toModifier()
                    .then(BodySmallTextStyle.toModifier())
                    .secondary(),
            )
        }
    }
}

@Composable
private fun KeyHolderRow(holder: KeyHolder) {
    KeyRow {
        KeyHolderChip(holder)
        KeyIcon(holder.isDeleted)
        if (holder.isDeleted) ErasedKeyRecord() else KeyRecord()
    }
}

@Composable
private fun KeyHolderChip(holder: KeyHolder) {
    val deleted = if (holder.isDeleted) {
        DeletedKeyHolderChipStyle
            .toModifier()
            .textDecorationLine(TextDecorationLine.LineThrough)
            .secondary()
    } else {
        Modifier
    }
    TextBlock(
        holder.name,
        KeyHolderChipStyle
            .toModifier()
            .then(BodySmallTextStyle.toModifier())
            .fontWeight(FontWeight.SemiBold)
            .then(deleted),
    )
}

@Composable
private fun KeyIcon(isRevoked: Boolean) {
    if (isRevoked) {
        TintedIcon(SiteRes.images.material_key_off, ColorToken.SecondaryText)
    } else {
        TintedIcon(SiteRes.images.material_key, ColorToken.ActionText)
    }
}

@Composable
private fun KeyRecord() {
    Row(KeyRecordStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
        TintedIcon(SiteRes.images.material_lock, ColorToken.SecondaryText, Modifier.size(1.cssRem))
        TextBlock(
            SiteRes.strings.site_privacy_keys_record.desc(),
            BodySmallTextStyle
                .toModifier()
                .secondary(),
        )
    }
}

@Composable
private fun ErasedKeyRecord() {
    P(
        KeyRecordStyle
            .toModifier()
            .then(ErasedKeyRecordStyle.toModifier())
            .then(CiphertextStyle.toModifier())
            .then(BodySmallTextStyle.toModifier())
            .secondary()
            .toAttrs(),
    ) {
        Span { Text(SiteRes.strings.site_privacy_keys_ciphertext.desc().localized()) }
        Text(" ")
        Text(SiteRes.strings.site_privacy_keys_erased.desc().localized())
    }
}
