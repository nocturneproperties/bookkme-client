package me.bookk.site.content

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import me.bookk.site.config.SiteBuildConfig
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
    const val ORIGIN = SiteBuildConfig.ORIGIN
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

    private const val SOURCE_ORIGIN = "https://github.com/nocturneproperties"

    val source = listOf(
        SiteLink(SiteRes.strings.site_footer_app_source.desc(), "$SOURCE_ORIGIN/bookkme-client"),
        SiteLink(SiteRes.strings.site_footer_server_source.desc(), "$SOURCE_ORIGIN/bookkme"),
        SiteLink(
            SiteRes.strings.site_footer_license.desc(),
            "$SOURCE_ORIGIN/bookkme-client/blob/main/LICENSE",
        ),
    )

    private fun contact(): SiteLink {
        return SiteLink(SiteRes.strings.site_nav_contact.desc(), "/contact")
    }

    fun copyright(): StringDesc {
        return SiteRes.strings.site_copyright.format(Date().getFullYear().toString())
    }
}
