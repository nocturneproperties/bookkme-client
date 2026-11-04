package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfirmedOperationsContractTest {
    @Test
    fun `StepUpRequestRemote pins its backend wire fields`() = runUnitTest {
        given()
        val descriptor = StepUpRequestRemote.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }

        then()
        assertEquals(listOf("requestId" to 1, "publicKeyCredentialJson" to 2, "action" to 3, "businessId" to 4, "targetId" to 5), fields)
    }
    @Test
    fun `StepUpTokenRemote pins its backend wire fields`() = runUnitTest {
        given()
        val descriptor = StepUpTokenRemote.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }

        then()
        assertEquals(listOf("token" to 1), fields)
    }
    @Test
    fun `SensitiveActionRemote ordinals match the backend`() = runUnitTest {
        given()
        val descriptor = SensitiveActionRemote.serializer().descriptor

        whenn()
        val names = (0 until descriptor.elementsCount).map { descriptor.getElementName(it) }

        then()
        assertEquals(listOf("DELETE_CLIENT", "EXPORT_PERSONAL_DATA", "EXPORT_BUSINESS_DATA"), names)
    }

}
