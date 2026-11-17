package me.bookk.site.content

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.resources.SiteRes

class ContactChannel(
    val title: StringDesc,
    val description: StringDesc,
    val email: String,
    val topics: List<StringDesc>,
)

object ContactContent {
    private const val SUPPORT_EMAIL = "support@bookkme.com"
    private const val LEGAL_EMAIL = "legal@bookkme.com"

    private const val SECURITY_EMAIL = "security@bookkme.com"

    val channels = listOf(
        ContactChannel(
            SiteRes.strings.site_contact_support_title.desc(),
            SiteRes.strings.site_contact_support_description.desc(),
            SUPPORT_EMAIL,
            listOf(
                SiteRes.strings.site_contact_support_topic_app.desc(),
                SiteRes.strings.site_contact_support_topic_access.desc(),
                SiteRes.strings.site_contact_support_topic_report.desc(),
                SiteRes.strings.site_contact_support_topic_complaints.desc(),
            ),
        ),
        ContactChannel(
            SiteRes.strings.site_contact_legal_title.desc(),
            SiteRes.strings.site_contact_legal_description.desc(),
            LEGAL_EMAIL,
            listOf(
                SiteRes.strings.site_contact_legal_topic_policy.desc(),
                SiteRes.strings.site_contact_legal_topic_rights.desc(),
                SiteRes.strings.site_contact_legal_topic_deletion.desc(),
                SiteRes.strings.site_contact_legal_topic_businesses.desc(),
            ),
        ),
        ContactChannel(
            SiteRes.strings.site_contact_security_title.desc(),
            SiteRes.strings.site_contact_security_description.desc(),
            SECURITY_EMAIL,
            listOf(
                SiteRes.strings.site_contact_security_topic_vulnerabilities.desc(),
                SiteRes.strings.site_contact_security_topic_breaches.desc(),
            ),
        ),
    )

    val appointmentQuestion = SiteRes.strings.site_contact_appointment_question.desc()

    val appointmentAnswer = SiteRes.strings.site_contact_appointment_answer.desc()

    val sensitiveNote = SiteRes.strings.site_contact_note_sensitive.desc()
}
