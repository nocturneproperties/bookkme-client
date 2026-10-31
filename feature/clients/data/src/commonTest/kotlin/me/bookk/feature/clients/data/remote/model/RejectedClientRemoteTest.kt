package me.bookk.feature.clients.data.remote.model

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.clients.domain.api.entity.RejectedClientImport
import me.bookk.feature.clients.domain.datasource.ClientErrorCodes
import kotlin.test.Test
import kotlin.test.assertEquals

class RejectedClientRemoteTest {

    @Test
    fun `maps validation error code to invalid fields`() = runUnitTest {
        given()
        val remote = RejectedClientRemote(index = 2, errorCode = ClientErrorCodes.CLIENT_VALIDATION_ERROR)

        whenn()
        val rejected = remote.toDomain()

        then()
        assertEquals(RejectedClientImport(2, RejectedClientImport.Reason.INVALID_FIELDS), rejected)
    }

    @Test
    fun `maps missing contact info error code to missing contact info`() = runUnitTest {
        given()
        val remote = RejectedClientRemote(index = 0, errorCode = ClientErrorCodes.CLIENT_MISSING_CONTACT_INFO)

        whenn()
        val rejected = remote.toDomain()

        then()
        assertEquals(RejectedClientImport(0, RejectedClientImport.Reason.MISSING_CONTACT_INFO), rejected)
    }

    @Test
    fun `maps client exists error code to already exists`() = runUnitTest {
        given()
        val remote = RejectedClientRemote(index = 7, errorCode = ClientErrorCodes.CLIENT_EXISTS)

        whenn()
        val rejected = remote.toDomain()

        then()
        assertEquals(RejectedClientImport(7, RejectedClientImport.Reason.ALREADY_EXISTS), rejected)
    }

    @Test
    fun `maps an unknown error code to unknown`() = runUnitTest {
        given()
        val remote = RejectedClientRemote(index = 1, errorCode = 999999)

        whenn()
        val rejected = remote.toDomain()

        then()
        assertEquals(RejectedClientImport(1, RejectedClientImport.Reason.UNKNOWN), rejected)
    }
}
