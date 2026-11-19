package me.bookk.site.content

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.resources.SiteRes

class ContactChannel(
    val id: String,
    val title: StringDesc,
    val description: StringDesc,
    val action: StringDesc,
    val email: String,
    val topics: List<StringDesc>,
)

object ContactContent {
    private const val SUPPORT_EMAIL = "support@bookkme.com"
    private const val LEGAL_EMAIL = "legal@bookkme.com"

    private const val SECURITY_EMAIL = "security@bookkme.com"

    val tips = listOf(
        Feature(
            SiteRes.strings.site_contact_appointment_question.desc(),
            SiteRes.strings.site_contact_appointment_answer.desc(),
        ),
        Feature(
            SiteRes.strings.site_contact_tip_deletion_title.desc(),
            SiteRes.strings.site_contact_tip_deletion_description.desc(),
        ),
        Feature(
            SiteRes.strings.site_contact_tip_sensitive_title.desc(),
            SiteRes.strings.site_contact_note_sensitive.desc(),
        ),
    )

    val channels = listOf(
        ContactChannel(
            "support",
            SiteRes.strings.site_contact_support_title.desc(),
            SiteRes.strings.site_contact_support_description.desc(),
            SiteRes.strings.site_contact_support_action.desc(),
            SUPPORT_EMAIL,
            listOf(
                SiteRes.strings.site_contact_support_topic_app.desc(),
                SiteRes.strings.site_contact_support_topic_access.desc(),
                SiteRes.strings.site_contact_support_topic_report.desc(),
                SiteRes.strings.site_contact_support_topic_complaints.desc(),
            ),
        ),
        ContactChannel(
            "legal",
            SiteRes.strings.site_contact_legal_title.desc(),
            SiteRes.strings.site_contact_legal_description.desc(),
            SiteRes.strings.site_contact_legal_action.desc(),
            LEGAL_EMAIL,
            listOf(
                SiteRes.strings.site_contact_legal_topic_policy.desc(),
                SiteRes.strings.site_contact_legal_topic_rights.desc(),
                SiteRes.strings.site_contact_legal_topic_deletion.desc(),
                SiteRes.strings.site_contact_legal_topic_businesses.desc(),
            ),
        ),
        ContactChannel(
            "security",
            SiteRes.strings.site_contact_security_title.desc(),
            SiteRes.strings.site_contact_security_description.desc(),
            SiteRes.strings.site_contact_security_action.desc(),
            SECURITY_EMAIL,
            listOf(
                SiteRes.strings.site_contact_security_topic_vulnerabilities.desc(),
                SiteRes.strings.site_contact_security_topic_breaches.desc(),
            ),
        ),
    )
}
