package me.bookk.feature.settings.presentation.editprofile

internal class FullName(
    val firstName: String,
    val lastName: String,
) {
    companion object {
        private val Whitespace = Regex("\\s+")

        fun parse(text: String): FullName {
            val words = text.trim().split(Whitespace)
            return FullName(firstName = words.first(), lastName = words.drop(1).joinToString(" "))
        }

        fun join(firstName: String, lastName: String): String {
            return "$firstName $lastName".trim()
        }
    }
}
