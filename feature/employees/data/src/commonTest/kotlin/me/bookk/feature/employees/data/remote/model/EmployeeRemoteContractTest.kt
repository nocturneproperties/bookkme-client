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
    fun `EmployeeInvitation field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = EmployeeInvitationRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf("id" to 1, "businessId" to 2, "invitedBy" to 3, "code" to 4, "status" to 5, "createdAt" to 6),
            fields
        )
    }

    @Test
    fun `EmployeeInvitationRedeemRequest field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = EmployeeInvitationRedeemRequest.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("code" to 1), fields)
    }

    @Test
    fun `ResourcePermission field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = ResourcePermissionRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("view" to 1, "update" to 2, "delete" to 3), fields)
    }

    @Test
    fun `BusinessPermissions field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = BusinessPermissionsRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf("business" to 1, "employees" to 2, "clients" to 3, "services" to 4, "appointments" to 5),
            fields
        )
    }
}
