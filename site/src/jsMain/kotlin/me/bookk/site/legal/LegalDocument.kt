package me.bookk.site.legal

import dev.icerock.moko.resources.StringResource
import me.bookk.site.resources.SiteRes

enum class LegalDocument(
    val slug: String,
    val title: StringResource,
    val description: StringResource,
) {
    PRIVACY_POLICY(
        "privacy",
        SiteRes.strings.site_privacy_policy,
        SiteRes.strings.site_privacy_policy_description,
    ),
    TERMS_OF_SERVICE(
        "terms",
        SiteRes.strings.site_terms_of_service,
        SiteRes.strings.site_terms_of_service_description,
    ),
    DATA_PROCESSING_AGREEMENT(
        "dpa",
        SiteRes.strings.site_data_processing_agreement,
        SiteRes.strings.site_data_processing_agreement_description,
    ),
    ;

    val path: String
        get() = "/$slug"
}
