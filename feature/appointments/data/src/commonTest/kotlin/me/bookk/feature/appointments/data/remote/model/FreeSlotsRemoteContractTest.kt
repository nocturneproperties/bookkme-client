package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class FreeSlotsRemoteContractTest {

    private fun SerialDescriptor.wireFields(): List<Pair<String, Int>> {
        return (0 until elementsCount).map {
            getElementName(it) to
                getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }
    }

    @Test
    fun `request wire fields match the slots route`() = runUnitTest {
        given()
        val descriptor = FreeSlotsRequestRemote.serializer().descriptor

        whenn()
        val fields = descriptor.wireFields()

        then()
        assertEquals(
            listOf("businessId" to 1, "employeeId" to 2, "services" to 3, "from" to 4, "to" to 5),
            fields,
        )
    }

    @Test
    fun `day wire fields match the slots route`() = runUnitTest {
        given()
        val descriptor = FreeSlotsDayRemote.serializer().descriptor

        whenn()
        val fields = descriptor.wireFields()

        then()
        assertEquals(listOf("date" to 1, "slots" to 2), fields)
    }

    @Test
    fun `slot wire fields match the slots route`() = runUnitTest {
        given()
        val descriptor = FreeSlotRemote.serializer().descriptor

        whenn()
        val fields = descriptor.wireFields()

        then()
        assertEquals(listOf("start" to 1, "end" to 2), fields)
    }
}
