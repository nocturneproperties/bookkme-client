package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class LegalDocumentVersionRemoteContractTest {
    @Test
    fun `wire fields match the monolith schema`() = runUnitTest {
        given()
        val descriptor = LegalDocumentVersionRemote.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }

        then()
        assertEquals(listOf("document" to 1, "version" to 2, "url" to 3, "effectiveAt" to 4), fields)
    }
}
