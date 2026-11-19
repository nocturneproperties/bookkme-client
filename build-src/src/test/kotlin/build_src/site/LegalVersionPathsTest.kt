package build_src.site

import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class LegalVersionPathsTest {
    private val legal: File = Files.createTempDirectory("legal").toFile()
    private val locked = mutableListOf<String>()

    private fun publish(document: String, version: String, isLocked: Boolean = true) {
        val directory = legal.resolve("$document/$version")
        directory.mkdirs()
        VERSION_FILES.forEach {
            directory.resolve(it).writeText("text")
            if (isLocked) locked += "$document/$version/$it"
        }
        writeLock()
    }

    private fun writeLock() {
        legal.resolve("published.sha256").writeText(locked.joinToString("") { "0000  $it\n" })
    }

    @Test
    fun `reads one path per published legal document version`() {
        publish("privacy", "2026-12-01")
        publish("privacy", "2027-03-01")
        publish("terms", "2026-12-01")

        val paths = LegalVersionPaths.read(legal)

        assertEquals(
            listOf("/privacy/2026-12-01", "/privacy/2027-03-01", "/terms/2026-12-01"),
            paths,
        )
    }

    @Test
    fun `skips a version that is not in the lock file`() {
        publish("privacy", "2026-12-01")
        publish("privacy", "2027-03-01", isLocked = false)

        val paths = LegalVersionPaths.read(legal)

        assertEquals(listOf("/privacy/2026-12-01"), paths)
    }

    @Test
    fun `skips a version with missing files`() {
        publish("privacy", "2026-12-01")
        legal.resolve("privacy/2026-12-01/pl.md").delete()

        val paths = LegalVersionPaths.read(legal)

        assertEquals(emptyList(), paths)
    }

    @Test
    fun `skips a folder of an unknown document`() {
        publish("cookies", "2027-01-01")

        val paths = LegalVersionPaths.read(legal)

        assertEquals(emptyList(), paths)
    }

    private companion object {
        val VERSION_FILES = listOf("en.md", "uk.md", "pl.md", "effective-at.txt")
    }
}
