package me.bookk.site.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.onClick
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.content.MailtoLink
import me.bookk.site.content.SupportQuestion
import me.bookk.site.content.SupportTask
import me.bookk.site.content.SupportTaskTarget
import me.bookk.site.designsystem.components.AppCard
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.TitleMediumTextStyle
import me.bookk.site.designsystem.theme.themed
import me.bookk.site.resources.SiteRes

@Composable
fun SupportTaskCard(task: SupportTask, onQuestionOpened: (SupportQuestion) -> Unit) {
    AppCard {
        Heading(task.title, HeadingLevel.H3, TitleMediumTextStyle.toModifier())
        TextBlock(
            task.description,
            BodyMediumTextStyle
                .toModifier()
                .color(ColorToken.SecondaryText.themed),
        )
        Spacer()
        when (val target = task.target) {
            is SupportTaskTarget.Question -> QuestionLink(target.question, onQuestionOpened)
            is SupportTaskTarget.Email -> EmailLink(target)
        }
    }
}

@Composable
private fun QuestionLink(question: SupportQuestion, onQuestionOpened: (SupportQuestion) -> Unit) {
    TextLink(
        SiteRes.strings.site_contact_task_see_steps.desc(),
        "#${question.id}",
        BodyMediumTextStyle
            .toModifier()
            .color(ColorToken.ActionText.themed)
            .onClick { onQuestionOpened(question) },
    )
}

@Composable
private fun EmailLink(target: SupportTaskTarget.Email) {
    TextLink(
        SiteRes.strings.site_contact_task_write.desc(),
        MailtoLink.build(target.email, target.subject.localized()),
        BodyMediumTextStyle
            .toModifier()
            .color(ColorToken.ActionText.themed),
    )
}
