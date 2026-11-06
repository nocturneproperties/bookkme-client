package library.files.impl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import okio.FileSystem
import okio.ForwardingFileSystem
import okio.IOException
import okio.Path
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class CommonFileProviderTest {
    private val resources = mutableListOf<Fixture>()
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        resources.forEach {
            it.fileSystem.deleteRecursively(it.directory, mustExist = false)
        }
        Dispatchers.resetMain()
    }

    private class FailingFileSystem : ForwardingFileSystem(FileSystem.SYSTEM) {
        var failAtomicMove = false

        override fun atomicMove(source: Path, target: Path) {
            if (failAtomicMove) {
                throw IOException("File replacement failed")
            }
            super.atomicMove(source, target)
        }
    }

    private class Fixture {
        val directory =
            FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "bookk-file-provider-test-${Uuid.random()}"
        val fileSystem = FailingFileSystem()
        val sut = object : CommonFileProvider(fileSystem) {
            override fun touchPrefsFile(fileName: String): Path {
                return directory / "prefs" / fileName
            }

            override fun touchCacheFile(fileName: String): Path {
                return directory / "cache" / fileName
            }
        }

        fun read(file: Path): String? {
            return file.takeIf { fileSystem.exists(it) }?.let {
                fileSystem.read(it) { readUtf8() }
            }
        }

        fun list(directory: Path): List<Path> {
            return fileSystem.listOrNull(directory).orEmpty()
        }
    }

    @Test
    fun `writes UTF-8 content and creates missing parent directories`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val file = fixture.sut.touchCacheFile("nested/export.json")
        val content = "{\"name\":\"Zażółć Україна\"}"

        whenn()
        fixture.sut.writeText(file, content)

        then()
        assertEquals(content, fixture.read(file))
    }

    @Test
    fun `missing file does not exist`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }

        whenn()
        val result = fixture.sut.exists(fixture.sut.touchCacheFile("missing.json"))

        then()
        assertFalse(result)
    }

    @Test
    fun `written file exists`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val file = fixture.sut.touchCacheFile("export.json")
        fixture.sut.writeText(file, "content")

        whenn()
        val result = fixture.sut.exists(file)

        then()
        assertTrue(result)
    }

    @Test
    fun `replaces existing content without leaving temporary files`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val file = fixture.sut.touchCacheFile("export.json")
        fixture.sut.writeText(file, "old")

        whenn()
        fixture.sut.writeText(file, "new")

        then()
        assertEquals("new", fixture.read(file))
        assertEquals(listOf(file), fixture.list(file.parent!!))
    }

    @Test
    fun `failed replacement preserves existing content and removes temporary files`() =
        runUnitTest {
            given()
            val fixture = Fixture().also { resources.add(it) }
            val file = fixture.sut.touchCacheFile("export.json")
            fixture.sut.writeText(file, "original")
            fixture.fileSystem.failAtomicMove = true

            whenn()
            val result = runCatching { fixture.sut.writeText(file, "replacement") }

            then()
            assertFailsWith<IOException> { result.getOrThrow() }
            assertEquals("original", fixture.read(file))
            assertEquals(listOf(file), fixture.list(file.parent!!))
        }

    @Test
    fun `failed initial write leaves no cached content or temporary files`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val file = fixture.sut.touchCacheFile("export.json")
        fixture.fileSystem.failAtomicMove = true

        whenn()
        val result = runCatching { fixture.sut.writeText(file, "content") }

        then()
        assertFailsWith<IOException> { result.getOrThrow() }
        assertNull(fixture.read(file))
        assertTrue(fixture.list(file.parent!!).isEmpty())
    }

    @Test
    fun `recursive deletion removes descendants and preserves sibling files`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val removed = fixture.sut.touchCacheFile("removed/nested/export.json")
        val retained = fixture.sut.touchCacheFile("retained.json")
        fixture.sut.writeText(removed, "remove")
        fixture.sut.writeText(retained, "keep")

        whenn()
        fixture.sut.deleteRecursively(fixture.sut.touchCacheFile("removed"))
        fixture.sut.deleteRecursively(fixture.sut.touchCacheFile("removed"))

        then()
        assertNull(fixture.read(removed))
        assertEquals("keep", fixture.read(retained))
    }
}
