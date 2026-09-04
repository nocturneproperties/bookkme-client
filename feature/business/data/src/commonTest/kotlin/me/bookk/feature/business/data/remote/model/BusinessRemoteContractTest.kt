package me.bookk.feature.business.data.remote.model

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
class BusinessRemoteContractTest {

    private fun SerialDescriptor.protoFields(): List<Pair<String, Int>> =
        (0 until elementsCount).map { i ->
            val number = getElementAnnotations(i).filterIsInstance<ProtoNumber>().single().number
            getElementName(i) to number
        }

    @Test
    fun `Business carries the schedule and matches the backend schema`() = runUnitTest {
        given()
        val descriptor = BusinessRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf(
                "id" to 1,
                "name" to 2,
                "description" to 3,
                "address" to 4,
                "timeZone" to 5,
                "location" to 6,
                "currencyCode" to 7,
                "socials" to 8,
                "schedule" to 9,
                "permissions" to 10
            ),
            fields
        )
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

    @Test
    fun `BusinessUpdateModel carries the schedule and matches the backend schema`() = runUnitTest {
        given()
        val descriptor = BusinessUpdateRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf(
                "id" to 1,
                "name" to 2,
                "description" to 3,
                "address" to 4,
                "location" to 5,
                "currencyCode" to 6,
                "timeZone" to 7,
                "socials" to 8,
                "schedule" to 9
            ),
            fields
        )
    }

    @Test
    fun `BusinessCreateRequest field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = CreateBusinessRequest.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("name" to 1, "currencyCode" to 2, "timeZone" to 3), fields)
    }

    @Test
    fun `Schedule carries day offs and matches the backend schema`() = runUnitTest {
        given()
        val descriptor = ScheduleRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("days" to 1, "dayOffs" to 2), fields)
    }

    @Test
    fun `DayOfWeekSchedule field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = DayOfWeekScheduleRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("workingTime" to 1, "isActive" to 2), fields)
    }

    @Test
    fun `WorkHour carries no day of week and matches the backend schema`() = runUnitTest {
        given()
        val descriptor = WorkHourRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("from" to 1, "to" to 2), fields)
    }

    @Test
    fun `DayOffRange field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = DayOffRangeRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("start" to 1, "end" to 2), fields)
    }
}
