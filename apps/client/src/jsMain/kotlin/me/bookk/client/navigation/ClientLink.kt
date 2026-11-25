package me.bookk.client.navigation

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.core.config.SiteUrl
import me.bookk.designsystem.resources.WebLocalization

class ClientLink(
    val title: StringDesc,
    val url: String,
) {
    companion object {
        const val SITE_CLIENTS_PATH = "/clients"
        const val SITE_CONTACT_PATH = "/contact"
        const val SITE_TERMS_PATH = "/terms"
        const val SITE_PRIVACY_PATH = "/privacy"

        fun siteClients(title: StringDesc, siteUrl: SiteUrl): ClientLink {
            return ClientLink(title, sitePage(SITE_CLIENTS_PATH, siteUrl))
        }

        fun sitePage(path: String, siteUrl: SiteUrl): String {
            return siteUrl.value + WebLocalization.localize(path)
        }
    }
}
