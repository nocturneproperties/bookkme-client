package library.files.impl

import library.files.api.FileProvider
import library.files.impl.di.filesModule
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsFileProviderTest {

    private class Fixture {
        val koin = koinApplication { modules(filesModule()) }.koin
    }

    @Test
    fun `keeps a written cache file for every later lookup of the provider`() = runUnitTest {
        given()
        val fixture = Fixture()
        val writer = fixture.koin.get<FileProvider>()
        val file = writer.touchCacheFile("exports") / "export.json"

        whenn()
        writer.writeText(file, "{}")

        then()
        assertTrue(fixture.koin.get<FileProvider>().exists(file))
    }

    @Test
    fun `deletes a cache directory with its files`() = runUnitTest {
        given()
        val fixture = Fixture()
        val provider = fixture.koin.get<FileProvider>()
        val directory = provider.touchCacheFile("exports")
        val file = directory / "export.json"
        provider.writeText(file, "{}")

        whenn()
        provider.deleteRecursively(directory)

        then()
        assertFalse(provider.exists(file))
    }

    @Test
    fun `keeps prefs files apart from cache files`() = runUnitTest {
        given()
        val fixture = Fixture()
        val provider = fixture.koin.get<FileProvider>()
        val prefsFile = provider.touchPrefsFile("settings")
        provider.writeText(prefsFile, "{}")

        whenn()
        provider.deleteRecursively(provider.touchCacheFile(""))

        then()
        assertTrue(provider.exists(prefsFile))
    }

    @Test
    fun `reads back the content of a written cache file`() = runUnitTest {
        given()
        val fixture = Fixture()
        val provider = fixture.koin.get<FileProvider>()
        val file = provider.touchCacheFile("exports") / "export.json"
        provider.writeText(file, "{\"name\":\"Zażółć\"}")

        whenn()
        val result = provider.readText(file)

        then()
        assertEquals("{\"name\":\"Zażółć\"}", result)
    }

    @Test
    fun `reads nothing from a file that was never written`() = runUnitTest {
        given()
        val fixture = Fixture()
        val provider = fixture.koin.get<FileProvider>()

        whenn()
        val result = provider.readText(provider.touchCacheFile("missing.json"))

        then()
        assertNull(result)
    }
}
