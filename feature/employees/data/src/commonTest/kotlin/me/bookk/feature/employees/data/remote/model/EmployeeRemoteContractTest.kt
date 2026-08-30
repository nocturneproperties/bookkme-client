package me.bookk.feature.employees.data.remote.model

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
 * cannot catch it, so these tests pin the declaration order to the published schema.
 *
 * Source: http://localhost/api/business/internal/swagger/documentation.yaml
 */
class EmployeeRemoteContractTest {

    private fun SerialDescriptor.fieldOrder(): List<String> =
        (0 until elementsCount).map { getElementName(it) }

    @Test
    fun `Employee field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = EmployeeRemote.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(
            listOf(
                "id",
                "businessId",
                "name",
                "lastName",
                "phone",
                "email",
                "userId",
                "services",
                "schedule",
                "createdAt"
            ),
            fields
        )
    }

    @Test
    fun `PromoteEmployeeRequest field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = PromoteEmployeeRequestRemote.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(listOf("role"), fields)
    }

    @Test
    fun `EmployeeInvitationRequest field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = EmployeeInvitationRequestRemote.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(listOf("email"), fields)
    }

    @Test
    fun `EmployeeInvitation field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = EmployeeInvitationRemote.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(
            listOf("id", "businessId", "invitedBy", "email", "status", "createdAt"),
            fields
        )
    }
}
