package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SignUpRemoteContractTest {
    @Test
    fun `signup appends required accepted documents at wire number five`() = runUnitTest {
        given()
        val descriptor = VerifyAccountCreationRequest.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }

        then()
        assertEquals(listOf("requestId" to 1, "deviceInfo" to 2, "userInfo" to 3, "publicKeyCredentialJson" to 4, "acceptedDocuments" to 5), fields)
    }
    @Test
    fun `accepted document versions use the backend wire numbers`() = runUnitTest {
        given()
        val descriptor = AcceptedLegalDocumentsRemote.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }

        then()
        assertEquals(listOf("privacyPolicy" to 1, "termsOfService" to 2), fields)
    }

    @Test
    fun `accepted documents remain a required signup field`() = runUnitTest {
        given()
        val descriptor = VerifyAccountCreationRequest.serializer().descriptor

        whenn()
        val optional = descriptor.isElementOptional(descriptor.getElementIndex("acceptedDocuments"))

        then()
        assertFalse(optional)
    }

}
