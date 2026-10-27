package me.bookk.feature.authorization.data.local

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload

internal data class PasskeyAssertion(
    val credentialId: String,
    val userHandle: String?
) {
    companion object {
        fun from(payload: PasskeyVerificationPayload): PasskeyAssertion {
            val json = Json.parseToJsonElement(payload.jsonPayload).jsonObject
            return PasskeyAssertion(
                credentialId = json.getValue("id").jsonPrimitive.content,
                userHandle = json.userHandle()
            )
        }

        private fun JsonObject.userHandle(): String? {
            return get("response")?.jsonObject
                ?.get("userHandle")
                ?.jsonPrimitive
                ?.content
                ?.takeIf { it.isNotEmpty() }
        }
    }
}
