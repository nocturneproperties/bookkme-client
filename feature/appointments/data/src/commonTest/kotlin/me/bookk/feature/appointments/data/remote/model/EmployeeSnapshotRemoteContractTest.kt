package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The API speaks `application/x-protobuf`. Every remote model pins its wire numbers explicitly
 * via `@ProtoNumber` — a new field must get the next unused number and an existing number must
 * never be reassigned, or the wire format silently desyncs from the published schema.
 *
 * Source: http://localhost/api/appointments/internal/swagger/documentation.yaml
 */
class EmployeeSnapshotRemoteContractTest {

    private fun SerialDescriptor.protoFields(): List<Pair<String, Int>> =
        (0 until elementsCount).map { i ->
            val number = getElementAnnotations(i).filterIsInstance<ProtoNumber>().single().number
            getElementName(i) to number
        }

    @Test
    fun `EmployeeSnapshot field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = EmployeeSnapshotRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("id" to 1, "userId" to 2, "fullName" to 3), fields)
    }
}
