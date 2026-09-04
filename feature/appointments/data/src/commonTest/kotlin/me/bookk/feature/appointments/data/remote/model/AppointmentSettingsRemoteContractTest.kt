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
class AppointmentSettingsRemoteContractTest {

    private fun SerialDescriptor.protoFields(): List<Pair<String, Int>> =
        (0 until elementsCount).map { i ->
            val number = getElementAnnotations(i).filterIsInstance<ProtoNumber>().single().number
            getElementName(i) to number
        }

    @Test
    fun `AppointmentSettings field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = AppointmentSettingsRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf(
                "id" to 1,
                "businessId" to 2,
                "timeZone" to 3,
                "schedule" to 4,
                "automaticApproval" to 5,
                "inBetweenBreakInMinutes" to 6,
                "appointmentNote" to 7,
                "permissions" to 8
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
    fun `AppointmentSettingsUpdate carries no schedule and matches the backend schema`() = runUnitTest {
        given()
        val descriptor = AppointmentSettingsUpdateRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf("businessId" to 1, "automaticApproval" to 2, "inBetweenBreakInMinutes" to 3, "appointmentNote" to 4),
            fields
        )
    }

    @Test
    fun `Schedule carries day offs and matches the backend schema`() = runUnitTest {
        given()
        val descriptor = WorkingScheduleRemote.serializer().descriptor

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
