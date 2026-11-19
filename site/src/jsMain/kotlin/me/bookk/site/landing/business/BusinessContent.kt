package me.bookk.site.landing.business

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.landing.Feature
import me.bookk.site.landing.KeyHolder
import me.bookk.site.navigation.SiteLink
import me.bookk.site.navigation.SiteNavigation
import me.bookk.site.resources.SiteRes

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
    val proofs: List<SiteLink>,
)

object BusinessContent {
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
            listOf(SiteNavigation.selfHosting),
        ),
        Opinion(
            SiteRes.strings.site_philosophy_source_title.desc(),
            SiteRes.strings.site_philosophy_source_description.desc(),
            listOf(SiteNavigation.appSource, SiteNavigation.serverSource),
        ),
        Opinion(
            SiteRes.strings.site_philosophy_lockin_title.desc(),
            SiteRes.strings.site_philosophy_lockin_description.desc(),
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
}
