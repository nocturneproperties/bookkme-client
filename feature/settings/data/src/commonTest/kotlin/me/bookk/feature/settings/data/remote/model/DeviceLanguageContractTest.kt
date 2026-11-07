package me.bookk.feature.settings.data.remote.model

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class DeviceLanguageContractTest {
    @Test
    fun `device language supports the backend Polish value at ordinal two`() = runUnitTest {
        given()
        val descriptor = DeviceLanguageRemote.serializer().descriptor

        whenn()
        val names = (0 until descriptor.elementsCount).map { descriptor.getElementName(it) }

        then()
        assertEquals(listOf("EN", "UK", "PL"), names)
        assertEquals("PL", DeviceLanguageRemote.entries[2].toDomain().name)
    }
}
