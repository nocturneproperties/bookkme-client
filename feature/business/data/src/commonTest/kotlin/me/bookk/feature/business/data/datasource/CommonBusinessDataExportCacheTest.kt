package me.bookk.feature.business.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Instant
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class CommonBusinessDataExportCacheTest {
    private val resources = mutableListOf<Fixture>()
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        resources.forEach {
            it.client.close()
            it.files.clear()
        }
        Dispatchers.resetMain()
    }

    private class Fixture {
        val files = TestExportFileProvider()
        val client = HttpClient(MockEngine { error("Cache operations must not use the network") })
        val dao = FakeBusinessDataExportDao()
        val sut = CommonDataExportDataSource(client, dao, files)
        val first = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val second = Uuid.parse("00000000-0000-0000-0000-000000000002")
        val ready = DataExport(
            DataExportStatus.READY,
            Instant.parse("2026-11-05T12:00:00.123Z"),
            Instant.parse("2026-11-08T12:00:00.456Z"),
            Instant.parse("2026-11-06T12:00:00.789Z"),
        )
    }

    @Test
    fun `missing cache emits null without using the network`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }

        whenn()
        val result = fixture.sut.observe(fixture.first).first()

        then()
        assertNull(result)
    }

    @Test
    fun `saved never requested status is distinct from a missing row`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }

        whenn()
        fixture.sut.saveInDb(fixture.first, DataExport(DataExportStatus.NONE))

        then()
        assertEquals(DataExport(DataExportStatus.NONE), fixture.sut.observe(fixture.first).first())
        assertEquals("NONE", fixture.dao.exports[fixture.first]?.status)
    }

    @Test
    fun `restores status and all timestamps after datasource recreation`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveInDb(fixture.first, fixture.ready)
        val recreated = CommonDataExportDataSource(fixture.client, fixture.dao, fixture.files)

        whenn()
        val result = recreated.observe(fixture.first).first()

        then()
        assertEquals(fixture.ready, result)
    }

    @Test
    fun `keeps a separate status for each business`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val running = DataExport(DataExportStatus.RUNNING)

        whenn()
        fixture.sut.saveInDb(fixture.first, fixture.ready)
        fixture.sut.saveInDb(fixture.second, running)

        then()
        assertEquals(fixture.ready, fixture.sut.observe(fixture.first).first())
        assertEquals(running, fixture.sut.observe(fixture.second).first())
        assertEquals(
            mapOf(fixture.first to fixture.ready, fixture.second to running),
            fixture.sut.getAllFromDb(),
        )
    }

    @Test
    fun `saving replaces the business status and notifies active observers`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val running = DataExport(DataExportStatus.RUNNING)
        fixture.sut.saveInDb(fixture.first, running)
        val emissions = mutableListOf<DataExport?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.observe(fixture.first).collect { emissions.add(it) }
        }

        whenn()
        fixture.sut.saveInDb(fixture.first, fixture.ready)

        then()
        assertEquals(listOf<DataExport?>(running, fixture.ready), emissions)
    }

    @Test
    fun `publishes an unchanged saved status so screen recovery can complete`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val status = DataExport(DataExportStatus.NONE)
        fixture.sut.saveInDb(fixture.first, status)
        val emissions = mutableListOf<DataExport?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.observe(fixture.first).collect { emissions.add(it) }
        }

        whenn()
        fixture.sut.saveInDb(fixture.first, status)

        then()
        assertEquals(listOf<DataExport?>(status, status), emissions)
    }

    @Test
    fun `deleting a business status keeps other businesses`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveInDb(fixture.first, fixture.ready)
        fixture.sut.saveInDb(fixture.second, fixture.ready)

        whenn()
        fixture.sut.deleteFromDb(fixture.first)

        then()
        assertNull(fixture.sut.observe(fixture.first).first())
        assertEquals(fixture.ready, fixture.sut.observe(fixture.second).first())
    }

    @Test
    fun `logout clears every saved status and notifies active observers`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveInDb(fixture.first, fixture.ready)
        fixture.sut.saveInDb(fixture.second, fixture.ready)
        val emissions = mutableListOf<DataExport?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.observe(fixture.first).collect { emissions.add(it) }
        }

        whenn()
        fixture.sut.doOnLogOut()

        then()
        assertEquals(emptyMap(), fixture.sut.getAllFromDb())
        assertEquals(listOf(fixture.ready, null), emissions)
    }

    @Test
    fun `saved file is found after datasource recreation`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val content = "{\"name\":\"Zażółć Україна\"}"
        val saved = fixture.sut.saveFile(fixture.first, fixture.ready, content)
        val recreated = CommonDataExportDataSource(fixture.client, fixture.dao, fixture.files)

        whenn()
        val result = recreated.getFile(fixture.first, fixture.ready)

        then()
        assertEquals(saved, result)
        assertEquals(content, fixture.files.files[saved])
    }

    @Test
    fun `stores the export as a named JSON file keyed by business request and expiry`() =
        runUnitTest {
            given()
            val fixture = Fixture().also { resources.add(it) }

            whenn()
            val result = fixture.sut.saveFile(fixture.first, fixture.ready, "{\"records\":[]}")

            then()
            val key = "${fixture.ready.requestedAt?.toEpochMilliseconds()}-" +
                "${fixture.ready.expiresAt?.toEpochMilliseconds()}"
            val expected = fixture.files.touchCacheFile("business_data_exports") /
                fixture.first.toString() / key / "bookk-business-data.json"
            assertEquals(expected, result)
            assertEquals("{\"records\":[]}", fixture.files.files[expected])
        }

    @Test
    fun `saving a new export file removes the previous one of the same business`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val previous = fixture.ready.copy(requestedAt = null)
        fixture.sut.saveFile(fixture.first, previous, "{\"previous\":true}")

        whenn()
        fixture.sut.saveFile(fixture.first, fixture.ready, "{}")

        then()
        assertNull(fixture.sut.getFile(fixture.first, previous))
        assertNotNull(fixture.sut.getFile(fixture.first, fixture.ready))
    }

    @Test
    fun `saving an export file keeps files of other businesses`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveFile(fixture.second, fixture.ready, "{\"second\":true}")

        whenn()
        fixture.sut.saveFile(fixture.first, fixture.ready, "{}")

        then()
        assertNotNull(fixture.sut.getFile(fixture.second, fixture.ready))
    }

    @Test
    fun `missing export file is a cache miss`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveFile(fixture.second, fixture.ready, "{}")

        whenn()
        val result = fixture.sut.getFile(fixture.first, fixture.ready)

        then()
        assertNull(result)
    }

    @Test
    fun `different request or expiry does not reuse a stored file`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveFile(fixture.first, fixture.ready, "{}")

        whenn()
        val results = listOf(
            fixture.ready.copy(requestedAt = null),
            fixture.ready.copy(expiresAt = null),
        ).map { fixture.sut.getFile(fixture.first, it) }

        then()
        assertEquals(listOf(null, null), results)
    }

    @Test
    fun `cleanup removes only the files of the given business`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveFile(fixture.first, fixture.ready, "{}")
        fixture.sut.saveFile(fixture.second, fixture.ready, "{}")

        whenn()
        fixture.sut.deleteFiles(fixture.first)

        then()
        assertNull(fixture.sut.getFile(fixture.first, fixture.ready))
        assertNotNull(fixture.sut.getFile(fixture.second, fixture.ready))
    }

    @Test
    fun `cleanup preserves files outside the business export directory`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val personalFile = fixture.files.touchCacheFile("personal_data_exports") / "export.json"
        fixture.files.writeText(personalFile, "{\"personal\":true}")
        fixture.sut.saveFile(fixture.first, fixture.ready, "{}")

        whenn()
        fixture.sut.deleteFiles(fixture.first)
        fixture.sut.doOnLogOut()

        then()
        assertEquals("{\"personal\":true}", fixture.files.files[personalFile])
    }

    @Test
    fun `business export can be cached again after cleanup`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveFile(fixture.first, fixture.ready, "{\"previous\":true}")
        fixture.sut.deleteFiles(fixture.first)

        whenn()
        fixture.sut.saveFile(fixture.first, fixture.ready, "{}")

        then()
        assertNotNull(fixture.sut.getFile(fixture.first, fixture.ready))
    }

    @Test
    fun `logout removes downloaded files of every business`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveFile(fixture.first, fixture.ready, "{}")
        fixture.sut.saveFile(fixture.second, fixture.ready, "{}")

        whenn()
        fixture.sut.doOnLogOut()

        then()
        assertNull(fixture.sut.getFile(fixture.first, fixture.ready))
        assertNull(fixture.sut.getFile(fixture.second, fixture.ready))
    }
}
