package me.bookk.core.data

import kotlinx.datetime.TimeZone
import kotlinx.datetime.UtcOffset
import kotlinx.datetime.offsetAt
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

@OptIn(ExperimentalSerializationApi::class)
class TimeZoneSerializerTest {

    @Serializable
    private class ZoneIdRemote(
        @ProtoNumber(1) val timeZone: String,
    )

    @Serializable
    private class ZoneRemote(
        @ProtoNumber(1)
        @Serializable(with = TimeZoneSerializer::class)
        val timeZone: TimeZone,
    )

    private fun decodeZone(id: String): TimeZone {
        val bytes = dataSerializer.encodeToByteArray(ZoneIdRemote(id))
        return dataSerializer.decodeFromByteArray<ZoneRemote>(bytes).timeZone
    }

    @Test
    fun `decodes a zone with summer time thirty years ahead`() = runUnitTest {
        given()
        val warsaw = decodeZone("Europe/Warsaw")

        whenn()
        val offset = warsaw.offsetAt(Instant.parse("2055-07-01T12:00:00Z"))

        then()
        assertEquals(UtcOffset(hours = 2), offset)
    }

    @Test
    fun `decodes a zone with winter time thirty years ahead`() = runUnitTest {
        given()
        val warsaw = decodeZone("Europe/Warsaw")

        whenn()
        val offset = warsaw.offsetAt(Instant.parse("2055-01-15T12:00:00Z"))

        then()
        assertEquals(UtcOffset(hours = 1), offset)
    }

    @Test
    fun `decodes a zone with summer time thirty years back`() = runUnitTest {
        given()
        val warsaw = decodeZone("Europe/Warsaw")

        whenn()
        val offset = warsaw.offsetAt(Instant.parse("1997-07-01T12:00:00Z"))

        then()
        assertEquals(UtcOffset(hours = 2), offset)
    }
}
