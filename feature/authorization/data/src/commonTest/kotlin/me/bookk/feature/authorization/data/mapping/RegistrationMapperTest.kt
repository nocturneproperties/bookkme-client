package me.bookk.feature.authorization.data.mapping

import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.data.remote.model.VerifyAccountCreationRequest
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationData
import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RegistrationMapperTest {
    private fun registration(documents: AcceptedLegalDocuments): RegistrationData {
        return RegistrationData(
            requestId = "request",
            deviceInfo = RegistrationData.DeviceInfo(
                "00000000-0000-0000-0000-000000000001",
                "Device",
            ),
            userInfo = RegistrationData.UserInfo("John", "Doe", "john@example.com"),
            publicKeyCredentialJson = "{}",
            acceptedDocuments = documents,
        )
    }

    @Test
    fun `serializes the versions explicitly accepted by the caller`() = runUnitTest {
        given()
        val data = registration(AcceptedLegalDocuments("privacy-v3", "terms-v2"))

        whenn()
        val bytes = dataSerializer.encodeToByteArray(
            VerifyAccountCreationRequest.serializer(),
            data.toRemote(),
        )
        val result = dataSerializer.decodeFromByteArray(
            VerifyAccountCreationRequest.serializer(),
            bytes,
        )

        then()
        assertEquals("privacy-v3", result.acceptedDocuments.privacyPolicy)
        assertEquals("terms-v2", result.acceptedDocuments.termsOfService)
    }

    @Test
    fun `keeps unaccepted documents absent on the wire`() = runUnitTest {
        given()
        val data = registration(AcceptedLegalDocuments(privacyPolicy = "privacy-v3"))

        whenn()
        val bytes = dataSerializer.encodeToByteArray(
            VerifyAccountCreationRequest.serializer(),
            data.toRemote(),
        )
        val result = dataSerializer.decodeFromByteArray(
            VerifyAccountCreationRequest.serializer(),
            bytes,
        )

        then()
        assertEquals("privacy-v3", result.acceptedDocuments.privacyPolicy)
        assertNull(result.acceptedDocuments.termsOfService)
    }

    @Test
    fun `includes the required acceptance container when no version was accepted`() = runUnitTest {
        given()
        val data = registration(AcceptedLegalDocuments())

        whenn()
        val bytes = dataSerializer.encodeToByteArray(
            VerifyAccountCreationRequest.serializer(),
            data.toRemote(),
        )
        val result = dataSerializer.decodeFromByteArray(
            VerifyAccountCreationRequest.serializer(),
            bytes,
        )

        then()
        assertNull(result.acceptedDocuments.privacyPolicy)
        assertNull(result.acceptedDocuments.termsOfService)
    }
}
