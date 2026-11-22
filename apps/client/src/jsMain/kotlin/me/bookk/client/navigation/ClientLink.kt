package me.bookk.client.navigation

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.core.config.SiteUrl
import me.bookk.site.designsystem.resources.WebLocalization

class ClientLink(
    val title: StringDesc,
    val url: String,
) {
    companion object {
        private const val SITE_CLIENTS_PATH = "/clients"

        fun siteClients(title: StringDesc, siteUrl: SiteUrl): ClientLink {
            return ClientLink(title, siteUrl.value + WebLocalization.localize(SITE_CLIENTS_PATH))
        }
    }
}
