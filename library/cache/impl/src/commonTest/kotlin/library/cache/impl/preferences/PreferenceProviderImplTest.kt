package library.cache.impl.preferences

import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class PreferenceProviderImplTest {
    private class Fixture {
        val requestedNames = mutableListOf<String>()
        val dataStore = FakePreferencesDataStore()
        val sut = PreferenceProviderImpl { name ->
            requestedNames += name
            dataStore
        }
    }

    @Test
    fun `opens the data store named after the bucket`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.get("clients_prefs")

        then()
        assertEquals(listOf("clients_prefs"), fixture.requestedNames)
    }

    @Test
    fun `stores values in the produced data store`() = runUnitTest {
        given()
        val fixture = Fixture()
        val key = Preferences.Key<String>("value")

        whenn()
        fixture.sut.get("clients_prefs").set(key, "stored")

        then()
        assertEquals("stored", PreferencesImpl(fixture.dataStore).get(key))
    }
}
