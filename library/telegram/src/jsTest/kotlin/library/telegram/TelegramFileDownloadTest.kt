package library.telegram

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TelegramFileDownloadTest {

    private class Fixture(
        version: String,
        accepts: Boolean = true,
    ) {
        val requests = mutableListOf<Pair<String, String>>()
        val webApp: TelegramWebApp = js("{}").unsafeCast<TelegramWebApp>().also { app ->
            val dynamicApp = app.asDynamic()
            dynamicApp.isVersionAtLeast = { minVersion: String ->
                minVersion.toVersion() <= version.toVersion()
            }
            dynamicApp.downloadFile =
                { params: TelegramDownloadFileParams, onClosed: (Boolean) -> Unit ->
                    requests += params.url to params.fileName
                    onClosed(accepts)
                }
        }

        private fun String.toVersion(): List<Int> {
            return split(".").map { it.toInt() }.let { it + List(2 - it.size) { 0 } }
        }

        private operator fun List<Int>.compareTo(other: List<Int>): Int {
            return zip(other).map { (a, b) -> a.compareTo(b) }.firstOrNull { it != 0 } ?: 0
        }
    }

    @Test
    fun `asks telegram to download the file under its name`() = runUnitTest {
        given()
        val fixture = Fixture(version = "8.0")
        val file = TelegramFileDownload("https://api.bookkme.com/export.json", "bookkme-data.json")

        whenn()
        fixture.webApp.download(file)

        then()
        assertEquals(
            listOf("https://api.bookkme.com/export.json" to "bookkme-data.json"),
            fixture.requests,
        )
    }

    @Test
    fun `reports a download the person accepted`() = runUnitTest {
        given()
        val fixture = Fixture(version = "8.0", accepts = true)

        whenn()
        val result = fixture.webApp.download(TelegramFileDownload("https://x.test/a", "a.json"))

        then()
        assertEquals(TelegramDownloadResult.ACCEPTED, result)
    }

    @Test
    fun `reports a download the person declined`() = runUnitTest {
        given()
        val fixture = Fixture(version = "9.1", accepts = false)

        whenn()
        val result = fixture.webApp.download(TelegramFileDownload("https://x.test/a", "a.json"))

        then()
        assertEquals(TelegramDownloadResult.DECLINED, result)
    }

    @Test
    fun `does not ask a telegram client without file downloads`() = runUnitTest {
        given()
        val fixture = Fixture(version = "7.10")

        whenn()
        val result = fixture.webApp.download(TelegramFileDownload("https://x.test/a", "a.json"))

        then()
        assertEquals(TelegramDownloadResult.UNSUPPORTED, result)
        assertTrue(fixture.requests.isEmpty())
    }

    @Test
    fun `sends the file name under the key telegram reads`() = runUnitTest {
        given()
        val file = TelegramFileDownload("https://x.test/a", "a.json")

        whenn()
        val params = file.toParams().asDynamic()

        then()
        assertEquals(listOf("https://x.test/a", "a.json"), listOf(params.url, params.file_name))
    }
}
