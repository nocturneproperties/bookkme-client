package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class EmailChangeRemoteContractTest {

    @Test
    fun `email change request uses the backend wire numbers`() = runUnitTest {
        given()
        val descriptor = EmailChangeRequestRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("email" to 1), fields)
    }

    @Test
    fun `email confirmation uses the backend wire numbers`() = runUnitTest {
        given()
        val descriptor = EmailConfirmationRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("verificationId" to 1, "code" to 2), fields)
    }

    @Test
    fun `email confirmation fields are required`() = runUnitTest {
        given()
        val descriptor = EmailConfirmationRemote.serializer().descriptor

        whenn()
        val optional = (0 until descriptor.elementsCount).map { descriptor.isElementOptional(it) }

        then()
        assertEquals(listOf(false, false), optional)
    }

    private fun SerialDescriptor.protoFields(): List<Pair<String, Int>> {
        return (0 until elementsCount).map {
            getElementName(it) to
                getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }
    }
}
