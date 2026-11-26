package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class PasskeyRemoteContractTest {
    @Test
    fun `passkey list pins the backend wire numbers`() = runUnitTest {
        given()
        val descriptor = PasskeyListRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("rpId" to 1, "userHandle" to 2, "passkeys" to 3), fields)
    }

    @Test
    fun `passkey pins the backend wire numbers`() = runUnitTest {
        given()
        val descriptor = PasskeyRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf(
                "id" to 1,
                "name" to 2,
                "createdAt" to 3,
                "lastUsedAt" to 4,
                "isBackedUp" to 5,
                "credentialId" to 6,
            ),
            fields,
        )
    }

    private fun SerialDescriptor.protoFields(): List<Pair<String, Int>> {
        return (0 until elementsCount).map {
            getElementName(it) to
                getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }
    }
}
