package me.bookk.site.legal

import dev.icerock.moko.resources.StringResource
import me.bookk.site.resources.SiteRes

enum class LegalDocument(
    val slug: String,
    val title: StringResource,
) {
    PRIVACY_POLICY("privacy", SiteRes.strings.site_privacy_policy),
    TERMS_OF_SERVICE("terms", SiteRes.strings.site_terms_of_service),
    DATA_PROCESSING_AGREEMENT("dpa", SiteRes.strings.site_data_processing_agreement),
    ;

    val path: String
        get() = "/$slug"
}
