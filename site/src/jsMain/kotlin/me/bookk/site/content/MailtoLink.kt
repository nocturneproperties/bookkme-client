package me.bookk.site.content

object MailtoLink {
    fun build(email: String, subject: String): String {
        if (subject.isBlank()) {
            return "mailto:$email"
        }
        return "mailto:$email?subject=${encodeURIComponent(subject)}"
    }
}

private external fun encodeURIComponent(value: String): String
