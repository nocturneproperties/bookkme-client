package me.bookk.feature.clients.data.remote.model

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The API speaks `application/x-protobuf`. Every remote model pins its wire numbers explicitly
 * via `@ProtoNumber` — a new field must get the next unused number and an existing number must
 * never be reassigned, or the wire format silently desyncs from the published schema.
 *
 * Source: http://localhost/api/business/internal/swagger/documentation.yaml
 */
class ClientRemoteContractTest {

    private fun SerialDescriptor.protoFields(): List<Pair<String, Int>> =
        (0 until elementsCount).map { i ->
            val number = getElementAnnotations(i).filterIsInstance<ProtoNumber>().single().number
            getElementName(i) to number
        }

    @Test
    fun `ClientRemote field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = ClientRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf("id" to 1, "name" to 2, "lastName" to 3, "phone" to 4, "email" to 5, "userId" to 6, "description" to 7),
            fields
        )
    }

    @Test
    fun `ClientUpdateRemote field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = ClientUpdateRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf("id" to 1, "name" to 2, "lastName" to 3, "phone" to 4, "email" to 5, "description" to 6),
            fields
        )
    }
}
