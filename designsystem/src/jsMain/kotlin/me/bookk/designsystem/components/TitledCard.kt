package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.theme.TitleMediumTextStyle

@Composable
fun TitledCard(
    title: StringDesc,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    AppCard(modifier) {
        Heading(title, HeadingLevel.H3, TitleMediumTextStyle.toModifier())
        content()
    }
}
