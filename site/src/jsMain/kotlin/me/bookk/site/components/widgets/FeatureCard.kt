package me.bookk.site.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.content.Feature
import me.bookk.site.designsystem.components.AppCard
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.TitleMediumTextStyle
import me.bookk.site.designsystem.theme.themed

@Composable
fun FeatureCard(feature: Feature) {
    AppCard {
        Heading(feature.title, HeadingLevel.H3, TitleMediumTextStyle.toModifier())
        TextBlock(
            feature.description,
            BodyMediumTextStyle
                .toModifier()
                .color(ColorToken.SecondaryText.themed),
        )
    }
}
