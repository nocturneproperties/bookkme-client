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
}

object SiteNavigation {
    const val GUIDES_PATH = "/guides"

    val header = listOf(
        SiteLink(SiteRes.strings.site_nav_guides.desc(), GUIDES_PATH),
        contact(),
    )

    val footer = listOf(
        SiteLink(SiteRes.strings.site_privacy_policy.desc(), "/privacy"),
        SiteLink(SiteRes.strings.site_terms_of_service.desc(), "/terms"),
        contact(),
    )

    private fun contact(): SiteLink {
        return SiteLink(SiteRes.strings.site_nav_contact.desc(), "/contact")
    }

    fun copyright(): StringDesc {
        return SiteRes.strings.site_copyright.format(Date().getFullYear().toString())
    }
}
