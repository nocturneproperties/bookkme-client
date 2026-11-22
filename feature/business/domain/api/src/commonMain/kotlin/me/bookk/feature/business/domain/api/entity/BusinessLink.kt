package me.bookk.feature.business.domain.api.entity

import kotlin.uuid.Uuid

data class BusinessLink(
    val businessId: Uuid,
    val name: String,
) {
    companion object {
        private val NAME_PATTERN = Regex("^(?=.{3,32}$)[a-z0-9]+(-[a-z0-9]+)*$")

        fun normalizeName(name: String): String {
            return name.trim().lowercase()
        }

        fun isValidName(name: String): Boolean {
            return NAME_PATTERN.matches(normalizeName(name))
        }
    }
}
