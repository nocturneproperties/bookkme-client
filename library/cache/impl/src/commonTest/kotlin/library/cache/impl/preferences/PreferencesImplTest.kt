package library.cache.impl.preferences

import kotlinx.coroutines.flow.first
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.getFlow
import library.cache.api.set
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PreferencesImplTest {
    private class Fixture {
        val dataStore = FakePreferencesDataStore()
        val sut = PreferencesImpl(dataStore)
    }

    @Test
    fun `returns the value stored under a key`() = runUnitTest {
        given()
        val fixture = Fixture()
        val key = Preferences.Key<Long>("last_synced_at")
        fixture.sut.set(key, 42L)

        whenn()
        val result = fixture.sut.get(key)

        then()
        assertEquals(42L, result)
    }

    @Test
    fun `returns null for a key that was never stored`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut.get(Preferences.Key<String>("missing"))

        then()
        assertNull(result)
    }

    @Test
    fun `keeps values of different types apart`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.set(Preferences.Key<Int>("count"), 3)
        fixture.sut.set(Preferences.Key<Boolean>("enabled"), true)
        fixture.sut.set(Preferences.Key<Double>("ratio"), 0.5)
        fixture.sut.set(Preferences.Key<ByteArray>("blob"), byteArrayOf(1, 2, 3))

        whenn()
        val count = fixture.sut.get(Preferences.Key<Int>("count"))
        val enabled = fixture.sut.get(Preferences.Key<Boolean>("enabled"))
        val ratio = fixture.sut.get(Preferences.Key<Double>("ratio"))
        val blob = fixture.sut.get(Preferences.Key<ByteArray>("blob"))

        then()
        assertEquals(3, count)
        assertEquals(true, enabled)
        assertEquals(0.5, ratio)
        assertContentEquals(byteArrayOf(1, 2, 3), blob)
    }

    @Test
    fun `removes the key when set to null`() = runUnitTest {
        given()
        val fixture = Fixture()
        val key = Preferences.Key<String>("token")
        fixture.sut.set(key, "abc")

        whenn()
        fixture.sut.set(key, null)

        then()
        assertNull(fixture.sut.get(key))
    }

    @Test
    fun `removes only the requested key`() = runUnitTest {
        given()
        val fixture = Fixture()
        val removed = Preferences.Key<String>("removed")
        val kept = Preferences.Key<String>("kept")
        fixture.sut.set(removed, "a")
        fixture.sut.set(kept, "b")

        whenn()
        fixture.sut.remove(removed, String::class)

        then()
        assertNull(fixture.sut.get(removed))
        assertEquals("b", fixture.sut.get(kept))
    }

    @Test
    fun `clear removes every key`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.set(Preferences.Key<String>("a"), "1")
        fixture.sut.set(Preferences.Key<Long>("b"), 2L)

        whenn()
        fixture.sut.clear()

        then()
        assertNull(fixture.sut.get(Preferences.Key<String>("a")))
        assertNull(fixture.sut.get(Preferences.Key<Long>("b")))
    }

    @Test
    fun `flow emits the latest stored value`() = runUnitTest {
        given()
        val fixture = Fixture()
        val key = Preferences.Key<String>("theme")
        fixture.sut.set(key, "dark")

        whenn()
        val result = fixture.sut.getFlow(key).first()

        then()
        assertEquals("dark", result)
    }
}
