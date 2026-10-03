package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class AppointmentRemoteContractTest {

    private fun SerialDescriptor.protoFields(): List<Pair<String, Int>> {
        return (0 until elementsCount).map { i ->
            val number = getElementAnnotations(i).filterIsInstance<ProtoNumber>().single().number
            getElementName(i) to number
        }
    }

    private fun SerialDescriptor.enumOrder(): List<String> {
        return (0 until elementsCount).map { getElementName(it) }
    }

    @Test
    fun `Appointment field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = AppointmentRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf(
                "id" to 1,
                "userId" to 2,
                "businessId" to 3,
                "employee" to 4,
                "client" to 5,
                "services" to 6,
                "status" to 7,
                "date" to 8,
                "note" to 9,
                "cancellationReason" to 10,
                "completedBy" to 11,
                "priceAdjustment" to 12
            ),
            fields
        )
    }

    @Test
    fun `AppointmentStatus ordinals match the backend enum`() = runUnitTest {
        given()
        val descriptor = AppointmentStatusRemote.serializer().descriptor

        whenn()
        val order = descriptor.enumOrder()

        then()
        assertEquals(listOf("SCHEDULED", "COMPLETED", "CANCELLED", "NO_SHOW"), order)
    }

    @Test
    fun `AppointmentCompletedBy ordinals match the backend enum`() = runUnitTest {
        given()
        val descriptor = AppointmentCompletedByRemote.serializer().descriptor

        whenn()
        val order = descriptor.enumOrder()

        then()
        assertEquals(listOf("SYSTEM", "USER"), order)
    }

    @Test
    fun `PriceAdjustment field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = PriceAdjustmentRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("additionalServices" to 1, "price" to 2, "reason" to 3), fields)
    }

    @Test
    fun `AppointmentUpdate carries only ids with the backend field numbers`() = runUnitTest {
        given()
        val descriptor = AppointmentUpdateRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf("id" to 1, "date" to 8, "note" to 9, "employeeId" to 13, "services" to 14),
            fields
        )
    }

    @Test
    fun `RequestedService field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = RequestedServiceRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("serviceId" to 1, "count" to 2), fields)
    }

    @Test
    fun `CompleteAppointmentRequest field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = CompleteAppointmentRequestRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("priceAdjustment" to 1), fields)
    }

    @Test
    fun `PriceAdjustmentDraft field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = PriceAdjustmentDraftRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("additionalServiceIds" to 1, "price" to 2, "reason" to 3), fields)
    }

    @Test
    fun `AppointmentRequestDraft field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = AppointmentRequestDraftRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf(
                "businessId" to 1,
                "employeeId" to 2,
                "services" to 3,
                "date" to 4,
                "note" to 5,
                "offerToken" to 6
            ),
            fields
        )
    }
}
