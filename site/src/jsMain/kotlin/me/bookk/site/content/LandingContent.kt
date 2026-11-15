package me.bookk.site.content

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.resources.SiteRes

class Feature(
    val title: StringDesc,
    val description: StringDesc,
)

object LandingContent {
    val features = listOf(
        Feature(
            SiteRes.strings.site_feature_appointments_title.desc(),
            SiteRes.strings.site_feature_appointments_description.desc(),
        ),
        Feature(
            SiteRes.strings.site_feature_requests_title.desc(),
            SiteRes.strings.site_feature_requests_description.desc(),
        ),
        Feature(
            SiteRes.strings.site_feature_clients_title.desc(),
            SiteRes.strings.site_feature_clients_description.desc(),
        ),
        Feature(
            SiteRes.strings.site_feature_services_title.desc(),
            SiteRes.strings.site_feature_services_description.desc(),
        ),
        Feature(
            SiteRes.strings.site_feature_team_title.desc(),
            SiteRes.strings.site_feature_team_description.desc(),
        ),
        Feature(
            SiteRes.strings.site_feature_dashboard_title.desc(),
            SiteRes.strings.site_feature_dashboard_description.desc(),
        ),
    )

    val privacy = listOf(
        Feature(
            SiteRes.strings.site_privacy_tracking_title.desc(),
            SiteRes.strings.site_privacy_tracking_description.desc(),
        ),
        Feature(
            SiteRes.strings.site_privacy_export_title.desc(),
            SiteRes.strings.site_privacy_export_description.desc(),
        ),
        Feature(
            SiteRes.strings.site_privacy_retention_title.desc(),
            SiteRes.strings.site_privacy_retention_description.desc(),
        ),
        Feature(
            SiteRes.strings.site_privacy_passkeys_title.desc(),
            SiteRes.strings.site_privacy_passkeys_description.desc(),
        ),
    )
}
