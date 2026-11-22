package me.bookk.client.layout

import androidx.compose.runtime.Composable
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.client.resources.ClientRes
import me.bookk.site.designsystem.head.HeadMeta
import me.bookk.site.designsystem.head.HeadTags
import me.bookk.site.designsystem.resources.localized

@Composable
fun ClientPageHead(title: StringDesc, description: StringDesc) {
    val localizedTitle = title.localized()
    HeadTags(
        "$localizedTitle · ${ClientRes.strings.client_app_name.desc().localized()}",
        listOf(
            HeadMeta.name("description", description.localized()),
            HeadMeta.name("robots", "noindex"),
        ),
        emptyList(),
    )
}
