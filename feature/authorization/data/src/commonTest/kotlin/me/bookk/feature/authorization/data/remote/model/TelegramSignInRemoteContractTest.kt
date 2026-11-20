package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class TelegramSignInRemoteContractTest {
    @Test
    fun `telegram sign in request pins the backend wire numbers`() = runUnitTest {
        given()
        val descriptor = TelegramSignInRequestRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf("initData" to 1, "deviceInfo" to 2, "acceptedDocuments" to 3),
            fields,
        )
    }

    @Test
    fun `telegram sign in device info pins the backend wire numbers`() = runUnitTest {
        given()
        val descriptor = TelegramSignInRequestRemote.serializer().descriptor.getElementDescriptor(1)

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(listOf("deviceUUID" to 1, "deviceName" to 2), fields)
    }

    @Test
    fun `accepted documents stay optional for a returning user`() = runUnitTest {
        given()
        val descriptor = TelegramSignInRequestRemote.serializer().descriptor

        whenn()
        val nullable = descriptor.getElementDescriptor(
            descriptor.getElementIndex("acceptedDocuments"),
        ).isNullable

        then()
        assertEquals(true, nullable)
    }

    private fun SerialDescriptor.protoFields(): List<Pair<String, Int>> {
        return (0 until elementsCount).map {
            getElementName(it) to
                getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }
    }
}
