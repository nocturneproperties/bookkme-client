package me.bookk.feature.clients.data.remote.model

import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfirmedOperationsContractTest {
    @Test
    fun `DeleteClientRequest pins its backend wire fields`() = runUnitTest {
        given()
        val descriptor = DeleteClientRequest.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to
                descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }

        then()
        assertEquals(listOf("confirmationToken" to 1), fields)
    }
}
