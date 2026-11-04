package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class ClientSnapshotRemoteContractTest {
    @Test
    fun `client snapshot retains the linked user field at wire number five`() = runUnitTest {
        given()
        val descriptor = ClientSnapshotRemote.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }

        then()
        assertEquals(listOf("id" to 1, "fullName" to 2, "phone" to 3, "email" to 4, "userId" to 5), fields)
    }
}
