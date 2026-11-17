package me.bookk.site.content

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.resources.SiteRes

class ContactTopic(
    val label: StringDesc,
    val subject: StringDesc,
)

class ContactChannel(
    val title: StringDesc,
    val description: StringDesc,
    val email: String,
    val topics: List<ContactTopic>,
) {
    val mailto: String
        get() = MailtoLink.build(email, "")
}

class SupportQuestion(
    val id: String,
    val question: StringDesc,
    val answer: StringDesc,
)

sealed interface SupportTaskTarget {
    class Question(
        val question: SupportQuestion,
    ) : SupportTaskTarget

    class Email(
        val email: String,
        val subject: StringDesc,
    ) : SupportTaskTarget
}

class SupportTask(
    val title: StringDesc,
    val description: StringDesc,
    val target: SupportTaskTarget,
)

object ContactContent {
    private const val SUPPORT_EMAIL = "support@bookkme.com"
    private const val LEGAL_EMAIL = "legal@bookkme.com"

    private val lostPasskey = SupportQuestion(
        "lost-passkey",
        SiteRes.strings.site_contact_faq_passkey_question.desc(),
        SiteRes.strings.site_contact_faq_passkey_answer.desc(),
    )

    private val exportData = SupportQuestion(
        "export-data",
        SiteRes.strings.site_contact_faq_export_question.desc(),
        SiteRes.strings.site_contact_faq_export_answer.desc(),
    )

    private val deleteAccount = SupportQuestion(
        "delete-account",
        SiteRes.strings.site_contact_faq_delete_question.desc(),
        SiteRes.strings.site_contact_faq_delete_answer.desc(),
    )

    private val reportSubject = SiteRes.strings.site_contact_support_subject_report.desc()

    val tasks = listOf(
        SupportTask(
            SiteRes.strings.site_contact_task_sign_in_title.desc(),
            SiteRes.strings.site_contact_task_sign_in_description.desc(),
            SupportTaskTarget.Question(lostPasskey),
        ),
        SupportTask(
            SiteRes.strings.site_contact_task_export_title.desc(),
            SiteRes.strings.site_contact_task_export_description.desc(),
            SupportTaskTarget.Question(exportData),
        ),
        SupportTask(
            SiteRes.strings.site_contact_task_delete_title.desc(),
            SiteRes.strings.site_contact_task_delete_description.desc(),
            SupportTaskTarget.Question(deleteAccount),
        ),
        SupportTask(
            SiteRes.strings.site_contact_task_report_title.desc(),
            SiteRes.strings.site_contact_task_report_description.desc(),
            SupportTaskTarget.Email(SUPPORT_EMAIL, reportSubject),
        ),
    )

    val questions = listOf(
        lostPasskey,
        exportData,
        deleteAccount,
        SupportQuestion(
            "add-employee",
            SiteRes.strings.site_contact_faq_employee_question.desc(),
            SiteRes.strings.site_contact_faq_employee_answer.desc(),
        ),
        SupportQuestion(
            "client-retention",
            SiteRes.strings.site_contact_faq_retention_question.desc(),
            SiteRes.strings.site_contact_faq_retention_answer.desc(),
        ),
        SupportQuestion(
            "crash-reports",
            SiteRes.strings.site_contact_faq_crash_question.desc(),
            SiteRes.strings.site_contact_faq_crash_answer.desc(),
        ),
    )

    val channels = listOf(
        ContactChannel(
            SiteRes.strings.site_contact_support_title.desc(),
            SiteRes.strings.site_contact_support_description.desc(),
            SUPPORT_EMAIL,
            listOf(
                ContactTopic(
                    SiteRes.strings.site_contact_support_topic_app.desc(),
                    SiteRes.strings.site_contact_support_subject_app.desc(),
                ),
                ContactTopic(
                    SiteRes.strings.site_contact_support_topic_access.desc(),
                    SiteRes.strings.site_contact_support_subject_access.desc(),
                ),
                ContactTopic(
                    SiteRes.strings.site_contact_support_topic_report.desc(),
                    reportSubject,
                ),
                ContactTopic(
                    SiteRes.strings.site_contact_support_topic_complaints.desc(),
                    SiteRes.strings.site_contact_support_subject_complaints.desc(),
                ),
            ),
        ),
        ContactChannel(
            SiteRes.strings.site_contact_legal_title.desc(),
            SiteRes.strings.site_contact_legal_description.desc(),
            LEGAL_EMAIL,
            listOf(
                ContactTopic(
                    SiteRes.strings.site_contact_legal_topic_policy.desc(),
                    SiteRes.strings.site_contact_legal_subject_policy.desc(),
                ),
                ContactTopic(
                    SiteRes.strings.site_contact_legal_topic_rights.desc(),
                    SiteRes.strings.site_contact_legal_subject_rights.desc(),
                ),
                ContactTopic(
                    SiteRes.strings.site_contact_legal_topic_deletion.desc(),
                    SiteRes.strings.site_contact_legal_subject_deletion.desc(),
                ),
                ContactTopic(
                    SiteRes.strings.site_contact_legal_topic_businesses.desc(),
                    SiteRes.strings.site_contact_legal_subject_businesses.desc(),
                ),
                ContactTopic(
                    SiteRes.strings.site_contact_legal_topic_security.desc(),
                    SiteRes.strings.site_contact_legal_subject_security.desc(),
                ),
            ),
        ),
    )

    val businessNote = SiteRes.strings.site_contact_note_business.desc()

    val sensitiveNote = SiteRes.strings.site_contact_note_sensitive.desc()

    val checklist = listOf(
        SiteRes.strings.site_contact_checklist_device.desc(),
        SiteRes.strings.site_contact_checklist_version.desc(),
        SiteRes.strings.site_contact_checklist_business.desc(),
        SiteRes.strings.site_contact_checklist_steps.desc(),
    )
}
