package me.bookk.site.navigation

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import me.bookk.site.config.SiteBuildConfig
import me.bookk.site.legal.LegalDocument
import me.bookk.site.resources.SiteRes
import kotlin.js.Date

class SiteLink(
    val title: StringDesc,
    val path: String,
)

object SiteNavigation {
    const val ORIGIN = SiteBuildConfig.ORIGIN
    const val HOME_PATH = "/"
    const val CLIENTS_PATH = "/clients"
    const val GUIDES_PATH = "/guides"
    const val CONTACT_PATH = "/contact"

    val audiences = listOf(
        SiteLink(SiteRes.strings.site_nav_business.desc(), HOME_PATH),
        SiteLink(SiteRes.strings.site_nav_clients.desc(), CLIENTS_PATH),
    )

    val header = listOf(
        SiteLink(SiteRes.strings.site_nav_guides.desc(), GUIDES_PATH),
        contact(),
    )

    val footer = listOf(
        SiteLink(SiteRes.strings.site_privacy_policy.desc(), LegalDocument.PRIVACY_POLICY.path),
        SiteLink(SiteRes.strings.site_terms_of_service.desc(), LegalDocument.TERMS_OF_SERVICE.path),
        contact(),
    )

    private const val SOURCE_ORIGIN = "https://github.com/nocturneproperties"

    val appSource =
        SiteLink(SiteRes.strings.site_footer_app_source.desc(), "$SOURCE_ORIGIN/bookkme-client")

    val serverSource =
        SiteLink(SiteRes.strings.site_footer_server_source.desc(), "$SOURCE_ORIGIN/bookkme")

    val selfHosting = SiteLink(
        SiteRes.strings.site_footer_self_hosting.desc(),
        "$SOURCE_ORIGIN/bookkme-client/blob/main/LICENSE-SELF-HOSTING.md",
    )

    val source = listOf(
        appSource,
        serverSource,
        SiteLink(
            SiteRes.strings.site_footer_license.desc(),
            "$SOURCE_ORIGIN/bookkme-client/blob/main/LICENSE",
        ),
        selfHosting,
    )

    private fun contact(): SiteLink {
        return SiteLink(SiteRes.strings.site_nav_contact.desc(), CONTACT_PATH)
    }

    fun copyright(): StringDesc {
        return SiteRes.strings.site_copyright.format(Date().getFullYear().toString())
    }
}
