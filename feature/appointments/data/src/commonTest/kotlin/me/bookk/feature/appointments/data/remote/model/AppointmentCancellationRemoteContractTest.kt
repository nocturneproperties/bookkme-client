package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class AppointmentCancellationRemoteContractTest {

    private fun SerialDescriptor.protoFields(): List<Pair<String, Int>> {
        return (0 until elementsCount).map { i ->
            val number = getElementAnnotations(i).filterIsInstance<ProtoNumber>().single().number
            getElementName(i) to number
        }
    }

    @Test
    fun `AppointmentCancellation field order matches the backend schema`() = runUnitTest {
        given()
        val descriptor = AppointmentCancellationRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("id" to 1, "reason" to 3), fields)
    }
}
