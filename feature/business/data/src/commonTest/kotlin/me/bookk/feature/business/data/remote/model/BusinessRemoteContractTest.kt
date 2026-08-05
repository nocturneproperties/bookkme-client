package me.bookk.feature.business.data.remote.model

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
 * Source: http://localhost/api/business/internal/swagger/documentation.yaml
 */
class BusinessRemoteContractTest {

    private fun SerialDescriptor.fieldOrder(): List<String> =
        (0 until elementsCount).map { getElementName(it) }

    @Test
    fun `Business carries the schedule and matches the backend schema`() = runUnitTest {
        given()
        val descriptor = BusinessRemote.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(
            listOf(
                "id",
                "name",
                "description",
                "address",
                "timeZone",
                "location",
                "currencyCode",
                "socials",
                "schedule"
            ),
            fields
        )
    }

    @Test
    fun `BusinessUpdateModel carries the schedule and matches the backend schema`() = runUnitTest {
        given()
        val descriptor = BusinessUpdateRemote.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(
            listOf(
                "id",
                "name",
                "description",
                "address",
                "location",
                "currencyCode",
                "timeZone",
                "socials",
                "schedule"
            ),
            fields
        )
    }

    @Test
    fun `BusinessCreateRequest field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = CreateBusinessRequest.serializer().descriptor

        whenn()
        val fields = descriptor.fieldOrder()

        then()
        assertEquals(listOf("name", "currencyCode", "timeZone"), fields)
    }

    @Test
    fun `Schedule carries day offs and matches the backend schema`() = runUnitTest {
        given()
        val descriptor = ScheduleRemote.serializer().descriptor

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
