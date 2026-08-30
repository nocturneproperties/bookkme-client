package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.descriptors.SerialDescriptor
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The API speaks `application/x-protobuf` and none of the remote models declare explicit
 * `@ProtoNumber`s, so protobuf field numbers are assigned positionally by declaration order.
 * A reordered, added or removed property silently desyncs every field after it — the compiler
 * cannot catch it, so this test pins the declaration order to the published schema.
 *
 * Source: http://localhost/api/appointments/internal/swagger/documentation.yaml
 */
class EmployeeSnapshotRemoteContractTest {

    private fun SerialDescriptor.fieldOrder(): List<String> =
        (0 until elementsCount).map { getElementName(it) }

    @Test
    fun `EmployeeSnapshot field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = EmployeeSnapshotRemote.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(listOf("id", "userId", "fullName"), fields)
    }
}
