package me.bookk.site.content

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.resources.SiteRes

class Feature(
    val title: StringDesc,
    val description: StringDesc,
)

enum class StepIllustration {
    SHARE_LINK,
    TIME_SLOTS,
    APPROVE_REQUEST,
}

class Step(
    val title: StringDesc,
    val description: StringDesc,
    val illustration: StepIllustration,
)

class TimeSlot(
    val hour: Int,
    val minute: Int,
    val isSelected: Boolean,
) {
    fun label(): String {
        return "${hour.twoDigits()}:${minute.twoDigits()}"
    }

    private fun Int.twoDigits(): String {
        return toString().padStart(2, '0')
    }
}

class Opinion(
    val title: StringDesc,
    val description: StringDesc,
    val marketplaceNote: StringDesc,
    val proofs: List<SiteLink>,
)

class Promise(
    val title: StringDesc,
    val description: StringDesc,
    val icon: ImageResource,
)

class KeyField(
    val label: StringDesc,
    val ciphertext: StringDesc,
)

class KeyHolder(
    val name: StringDesc,
    val isDeleted: Boolean,
)

object LandingContent {
    val steps = listOf(
        Step(
            SiteRes.strings.site_step_share_title.desc(),
            SiteRes.strings.site_step_share_description.desc(),
            StepIllustration.SHARE_LINK,
        ),
        Step(
            SiteRes.strings.site_step_request_title.desc(),
            SiteRes.strings.site_step_request_description.desc(),
            StepIllustration.TIME_SLOTS,
        ),
        Step(
            SiteRes.strings.site_step_approve_title.desc(),
            SiteRes.strings.site_step_approve_description.desc(),
            StepIllustration.APPROVE_REQUEST,
        ),
    )

    val sampleTimeSlots = listOf(
        TimeSlot(11, 30, isSelected = false),
        TimeSlot(13, 0, isSelected = false),
        TimeSlot(15, 30, isSelected = true),
    )

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

    val opinions = listOf(
        Opinion(
            SiteRes.strings.site_philosophy_selfhost_title.desc(),
            SiteRes.strings.site_philosophy_selfhost_description.desc(),
            SiteRes.strings.site_opinion_runs_marketplace.desc(),
            listOf(SiteNavigation.selfHosting),
        ),
        Opinion(
            SiteRes.strings.site_philosophy_source_title.desc(),
            SiteRes.strings.site_philosophy_source_description.desc(),
            SiteRes.strings.site_opinion_data_marketplace.desc(),
            listOf(SiteNavigation.appSource, SiteNavigation.serverSource),
        ),
        Opinion(
            SiteRes.strings.site_philosophy_lockin_title.desc(),
            SiteRes.strings.site_philosophy_lockin_description.desc(),
            SiteRes.strings.site_opinion_leaving_marketplace.desc(),
            emptyList(),
        ),
    )

    val privacy = listOf(
        Feature(
            SiteRes.strings.site_privacy_tracking_title.desc(),
            SiteRes.strings.site_privacy_tracking_description.desc(),
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

    val keyHolders = listOf(
        KeyHolder(SiteRes.strings.site_privacy_keys_sample_first.desc(), isDeleted = false),
        KeyHolder(SiteRes.strings.site_privacy_keys_sample_second.desc(), isDeleted = false),
        KeyHolder(SiteRes.strings.site_privacy_keys_sample_deleted.desc(), isDeleted = true),
    )

    val personalKeyFields = listOf(
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

    val clientPrivacy = listOf(
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

    val clientPromises = listOf(
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
