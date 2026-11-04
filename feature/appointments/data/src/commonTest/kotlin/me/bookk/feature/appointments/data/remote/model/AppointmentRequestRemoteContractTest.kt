package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class AppointmentRequestRemoteContractTest {
    @Test
    fun `wire fields match the monolith schema`() = runUnitTest {
        given()
        val descriptor = AppointmentRequestRemote.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }

        then()
        assertEquals(listOf("id" to 1, "initiatorUserId" to 2, "businessId" to 3, "employee" to 4, "client" to 5, "services" to 6, "status" to 7, "date" to 8, "note" to 9, "declineReason" to 10), fields)
    }
}
