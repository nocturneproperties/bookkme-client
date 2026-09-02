package me.bookk.feature.employees.data.remote.model

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
 * Source: http://localhost/api/business/internal/swagger/documentation.yaml
 */
class EmployeeRemoteContractTest {

    private fun SerialDescriptor.protoFields(): List<Pair<String, Int>> =
        (0 until elementsCount).map { i ->
            val number = getElementAnnotations(i).filterIsInstance<ProtoNumber>().single().number
            getElementName(i) to number
        }

    @Test
    fun `Employee field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = EmployeeRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf(
                "id" to 1,
                "businessId" to 2,
                "name" to 3,
                "lastName" to 4,
                "phone" to 5,
                "email" to 6,
                "userId" to 7,
                "services" to 8,
                "schedule" to 9,
                "createdAt" to 10
            ),
            fields
        )
    }

    @Test
    fun `PromoteEmployeeRequest field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = PromoteEmployeeRequestRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("role" to 1), fields)
    }

    @Test
    fun `EmployeeInvitationRequest field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = EmployeeInvitationRequestRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("email" to 1), fields)
    }

    @Test
    fun `EmployeeInvitation field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = EmployeeInvitationRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf("id" to 1, "businessId" to 2, "invitedBy" to 3, "email" to 4, "status" to 5, "createdAt" to 6),
            fields
        )
    }
}
