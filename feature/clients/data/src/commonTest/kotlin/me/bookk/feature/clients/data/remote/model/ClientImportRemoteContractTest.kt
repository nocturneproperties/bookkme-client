package me.bookk.feature.clients.data.remote.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.decodeFromByteArray
import me.bookk.core.data.dataSerializer
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalSerializationApi::class)
class ClientImportRemoteContractTest {

    private fun SerialDescriptor.protoFields(): List<Pair<String, Int>> {
        return (0 until elementsCount).map { i ->
            val number = getElementAnnotations(i).filterIsInstance<ProtoNumber>().single().number
            getElementName(i) to number
        }
    }

    @Test
    fun `ClientImportRequest field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = ClientImportRequest.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("clients" to 1), fields)
    }

    @Test
    fun `ClientImportEntryRemote field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = ClientImportEntryRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf("name" to 1, "lastName" to 2, "phone" to 3, "email" to 4, "description" to 5),
            fields
        )
    }

    @Test
    fun `ClientImportResultRemote field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = ClientImportResultRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("created" to 1, "rejected" to 2), fields)
    }

    @Test
    fun `RejectedClientRemote field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = RejectedClientRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("index" to 1, "errorCode" to 2), fields)
    }

    @Test
    fun `decodes a result whose created and rejected lists are both empty`() = runUnitTest {
        given()
        val emptyMessage = ByteArray(0)

        whenn()
        val result = dataSerializer.decodeFromByteArray<ClientImportResultRemote>(emptyMessage)

        then()
        assertTrue(result.created.isEmpty())
        assertTrue(result.rejected.isEmpty())
    }
}
