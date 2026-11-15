package me.bookk.site.content

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import me.bookk.site.resources.SiteRes
import kotlin.js.Date

class SiteLink(
    val title: StringDesc,
    val path: String,
)

enum class LandingSection(
    val id: String,
) {
    FEATURES("features"),
    PRIVACY("privacy"),
    ;

    val path: String
        get() = "/#$id"
}

object SiteNavigation {
    val header = listOf(
        SiteLink(SiteRes.strings.site_nav_features.desc(), LandingSection.FEATURES.path),
        SiteLink(SiteRes.strings.site_nav_privacy.desc(), LandingSection.PRIVACY.path),
        SiteLink(SiteRes.strings.site_nav_guides.desc(), "/guides"),
    )

    val legal = listOf(
        SiteLink(SiteRes.strings.site_privacy_policy.desc(), "/privacy"),
        SiteLink(SiteRes.strings.site_terms_of_use.desc(), "/terms"),
    )

    fun copyright(): StringDesc {
        return SiteRes.strings.site_copyright.format(Date().getFullYear().toString())
    }
}
