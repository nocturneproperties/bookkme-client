package me.bookk.feature.settings.data.mapping

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.settings.data.remote.model.PasskeyListRemote
import me.bookk.feature.settings.data.remote.model.PasskeyRemote
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlin.uuid.Uuid

class PasskeyListMappingTest {
    private fun passkey(name: String, credentialId: String) = PasskeyRemote(
        id = Uuid.random(),
        name = name,
        createdAt = Instant.fromEpochMilliseconds(0),
        lastUsedAt = Instant.fromEpochMilliseconds(0),
        isBackedUp = true,
        credentialId = credentialId,
    )

    @Test
    fun `keeps what the device needs to know which passkeys are still accepted`() = runUnitTest {
        given()
        val remote = PasskeyListRemote(
            rpId = "bookkme.app",
            userHandle = "dXNlcg",
            passkeys = listOf(passkey("iPhone", "AAECAw"), passkey("Mac", "BAUGBw")),
        )

        whenn()
        val account = remote.toDomain()

        then()
        assertEquals("bookkme.app", account.relyingParty)
        assertEquals("dXNlcg", account.userHandle)
        assertEquals(listOf("AAECAw", "BAUGBw"), account.credentialIds)
        assertEquals(listOf("iPhone", "Mac"), account.passkeys.map { it.name })
    }
}
