package me.bookk.feature.employees.data.remote.model

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
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
                "createdAt" to 10,
                "permissions" to 11
            ),
            fields
        )
    }

    @Test
    fun `EmployeeUpdateModel field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = EmployeeUpdateRequest.serializer().descriptor

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
                "services" to 7,
                "schedule" to 8
            ),
            fields
        )
    }

    @Test
    fun `EmployeePermissionsRequest field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = EmployeePermissionsRequest.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf("business" to 1, "employees" to 2, "clients" to 3, "services" to 4, "appointments" to 5),
            fields
        )
    }

    @Test
    fun `permissions request writes every resource even when all its flags are revoked`() = runUnitTest {
        given()
        val revoked = ResourcePermission()
        val request = EmployeePermissionsRequest.fromDomain(
            BusinessPermissions(revoked, revoked, revoked, revoked, revoked)
        )
        val revokedResource = listOf<Byte>(6, 8, 0, 16, 0, 24, 0)

        whenn()
        val bytes = dataSerializer.encodeToByteArray(EmployeePermissionsRequest.serializer(), request)

        then()
        assertEquals((1..5).flatMap { listOf((it shl 3 or 2).toByte()) + revokedResource }, bytes.toList())
    }

    @Test
    fun `revoked permission flags are still written to the wire`() = runUnitTest {
        given()
        val permission = ResourcePermissionRemote(view = false, update = false, delete = false)

        whenn()
        val bytes = dataSerializer.encodeToByteArray(ResourcePermissionRemote.serializer(), permission)

        then()
        assertEquals(listOf<Byte>(8, 0, 16, 0, 24, 0), bytes.toList())
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
