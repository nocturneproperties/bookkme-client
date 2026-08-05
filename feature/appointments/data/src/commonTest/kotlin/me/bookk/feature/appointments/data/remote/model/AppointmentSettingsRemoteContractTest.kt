package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.descriptors.SerialDescriptor
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The API speaks `application/x-protobuf` and none of the remote models declare explicit
 * `@ProtoNumber`s, so protobuf field numbers are assigned positionally by declaration order.
 * A reordered, added or removed property silently desyncs every field after it — the compiler
 * cannot catch it, so these tests pin the declaration order to the published schema.
 *
 * Source: http://localhost/api/appointments/internal/swagger/documentation.yaml
 */
class AppointmentSettingsRemoteContractTest {

    private fun SerialDescriptor.fieldOrder(): List<String> =
        (0 until elementsCount).map { getElementName(it) }

    @Test
    fun `AppointmentSettings field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = AppointmentSettingsRemote.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(
            listOf(
                "id",
                "businessId",
                "timeZone",
                "schedule",
                "automaticApproval",
                "inBetweenBreakInMinutes",
                "appointmentNote"
            ),
            fields
        )
    }

    @Test
    fun `AppointmentSettingsUpdate carries no schedule and matches the backend schema`() = runUnitTest {
        given()
        val descriptor = AppointmentSettingsUpdateRemote.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(
            listOf("businessId", "automaticApproval", "inBetweenBreakInMinutes", "appointmentNote"),
            fields
        )
    }

    @Test
    fun `Schedule carries day offs and matches the backend schema`() = runUnitTest {
        given()
        val descriptor = WorkingScheduleRemote.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(listOf("days", "dayOffs"), fields)
    }

    @Test
    fun `DayOfWeekSchedule field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = DayOfWeekScheduleRemote.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(listOf("workingTime", "isActive"), fields)
    }

    @Test
    fun `WorkHour carries no day of week and matches the backend schema`() = runUnitTest {
        given()
        val descriptor = WorkHourRemote.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(listOf("from", "to"), fields)
    }

    @Test
    fun `DayOffRange field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = DayOffRangeRemote.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(listOf("start", "end"), fields)
    }
}
