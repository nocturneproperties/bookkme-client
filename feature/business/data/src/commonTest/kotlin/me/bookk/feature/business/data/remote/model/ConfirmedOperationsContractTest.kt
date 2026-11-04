package me.bookk.feature.business.data.remote.model

import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfirmedOperationsContractTest {
    @Test
    fun `DataExportRemote pins its backend wire fields`() = runUnitTest {
        given()
        val descriptor = DataExportRemote.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to
                descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }

        then()
        assertEquals(
            listOf(
                "status" to 1,
                "requestedAt" to 2,
                "expiresAt" to 3,
                "availableAgainAt" to 4,
            ),
            fields,
        )
    }

    @Test
    fun `DataExportRequestRemote pins its backend wire fields`() = runUnitTest {
        given()
        val descriptor = DataExportRequestRemote.serializer().descriptor

        whenn()
        val fields = (0 until descriptor.elementsCount).map {
            descriptor.getElementName(it) to
                descriptor.getElementAnnotations(it).filterIsInstance<ProtoNumber>().single().number
        }

        then()
        assertEquals(listOf("stepUpToken" to 1), fields)
    }

    @Test
    fun `DataExportStatusRemote ordinals match the backend`() = runUnitTest {
        given()
        val descriptor = DataExportStatusRemote.serializer().descriptor

        whenn()
        val names = (0 until descriptor.elementsCount).map { descriptor.getElementName(it) }

        then()
        assertEquals(listOf("NONE", "RUNNING", "READY", "FAILED"), names)
    }
}
