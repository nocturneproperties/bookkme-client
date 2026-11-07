package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfirmedOperationsContractTest {
    @Test
    fun `InstantAppointmentDraftRemote pins its backend wire fields`() = runUnitTest {
        given()
        val descriptor = InstantAppointmentDraftRemote.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to
                descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }

        then()
        assertEquals(
            listOf(
                "businessId" to 1,
                "employeeId" to 2,
                "services" to 3,
                "date" to 4,
                "note" to 5,
                "offerToken" to 6,
                "clientId" to 7,
            ),
            fields,
        )
    }
}
