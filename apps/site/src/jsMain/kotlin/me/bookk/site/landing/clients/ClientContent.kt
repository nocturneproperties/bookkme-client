package me.bookk.site.landing.clients

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.landing.Feature
import me.bookk.site.landing.KeyField
import me.bookk.site.resources.SiteRes

class Promise(
    val title: StringDesc,
    val description: StringDesc,
    val icon: ImageResource,
)

object ClientContent {
    val keyFields = listOf(
        KeyField(
            SiteRes.strings.site_clients_keys_field_name.desc(),
            SiteRes.strings.site_clients_keys_cipher_name.desc(),
        ),
        KeyField(
            SiteRes.strings.site_clients_keys_field_email.desc(),
            SiteRes.strings.site_clients_keys_cipher_email.desc(),
        ),
        KeyField(
            SiteRes.strings.site_clients_keys_field_phone.desc(),
            SiteRes.strings.site_clients_keys_cipher_phone.desc(),
        ),
        KeyField(
            SiteRes.strings.site_clients_keys_field_notes.desc(),
            SiteRes.strings.site_clients_keys_cipher_notes.desc(),
        ),
    )

    val privacy = listOf(
        Feature(
            SiteRes.strings.site_privacy_passkeys_title.desc(),
            SiteRes.strings.site_privacy_passkeys_description.desc(),
        ),
        Feature(
            SiteRes.strings.site_privacy_tracking_title.desc(),
            SiteRes.strings.site_clients_privacy_tracking_description.desc(),
        ),
        Feature(
            SiteRes.strings.site_clients_privacy_retention_title.desc(),
            SiteRes.strings.site_clients_privacy_retention_description.desc(),
        ),
    )

    val promises = listOf(
        Promise(
            SiteRes.strings.site_clients_product_title.desc(),
            SiteRes.strings.site_clients_product_description.desc(),
            SiteRes.images.material_visibility_off,
        ),
        Promise(
            SiteRes.strings.site_clients_direct_title.desc(),
            SiteRes.strings.site_clients_direct_description.desc(),
            SiteRes.images.material_handshake,
        ),
    )
}
