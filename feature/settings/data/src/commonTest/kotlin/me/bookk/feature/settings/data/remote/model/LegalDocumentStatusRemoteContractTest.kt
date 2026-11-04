package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class LegalDocumentStatusRemoteContractTest {
    @Test
    fun `wire fields match the monolith schema`() = runUnitTest {
        given()
        val descriptor = LegalDocumentStatusRemote.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }

        then()
        assertEquals(listOf("current" to 1, "acceptedAt" to 2), fields)
    }
}
