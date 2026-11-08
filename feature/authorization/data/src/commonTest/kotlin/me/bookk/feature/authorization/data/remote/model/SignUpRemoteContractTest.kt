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
    fun `signup request pins the backend wire numbers`() = runUnitTest {
        given()
        val descriptor = VerifyAccountCreationRequest.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to
                descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }

        then()
        assertEquals(
            listOf(
                "requestId" to 1,
                "deviceInfo" to 2,
                "userInfo" to 3,
                "publicKeyCredentialJson" to 4,
                "acceptedDocuments" to 5,
                "verificationId" to 6,
                "emailCode" to 7,
            ),
            fields,
        )
    }

    @Test
    fun `accepted document versions use the backend wire numbers`() = runUnitTest {
        given()
        val descriptor = AcceptedLegalDocumentsRemote.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to
                descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
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

    @Test
    fun `signup challenge appends email verification at wire number six`() = runUnitTest {
        given()
        val descriptor = SignUpChallengeResponseRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf(
                "requestId" to 1,
                "challenge" to 2,
                "challengeJson" to 3,
                "userHandle" to 4,
                "displayName" to 5,
                "emailVerification" to 6,
            ),
            fields,
        )
    }

    @Test
    fun `email verification started uses the backend wire numbers`() = runUnitTest {
        given()
        val descriptor = EmailVerificationStartedRemote.serializer().descriptor

        whenn()
        val fields = descriptor.protoFields()

        then()
        assertEquals(
            listOf("verificationId" to 1, "expiresAt" to 2, "resendAvailableAt" to 3),
            fields,
        )
    }

    @Test
    fun `verification id and email code remain required signup fields`() = runUnitTest {
        given()
        val descriptor = VerifyAccountCreationRequest.serializer().descriptor

        whenn()
        val optional = listOf("verificationId", "emailCode").map {
            descriptor.isElementOptional(descriptor.getElementIndex(it))
        }

        then()
        assertEquals(listOf(false, false), optional)
    }

    private fun kotlinx.serialization.descriptors.SerialDescriptor.protoFields(): List<Pair<String, Int>> {
        return (0 until elementsCount).map {
            getElementName(it) to
                getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }
    }
}
