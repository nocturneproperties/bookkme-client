package me.bookk.feature.settings.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
import me.bookk.database.dao.PersonalDataExportDao
import me.bookk.database.entity.PersonalDataExportEntity
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class CommonPersonalDataExportCacheTest {
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

    private class FakePersonalDataExportDao : PersonalDataExportDao() {
        private val snapshots = MutableSharedFlow<PersonalDataExportEntity?>(
            replay = 1,
            extraBufferCapacity = 1,
        ).apply { tryEmit(null) }
        val export: PersonalDataExportEntity?
            get() {
                return snapshots.replayCache.last()
            }

        override fun observe(): Flow<PersonalDataExportEntity?> {
            return snapshots
        }

        override suspend fun upsert(export: PersonalDataExportEntity) {
            check(snapshots.tryEmit(export))
        }

        override suspend fun clear() {
            check(snapshots.tryEmit(null))
        }
    }

    private class Fixture {
        val files = TestExportFileProvider()
        val client = HttpClient(MockEngine { error("Cache operations must not use the network") })
        val dao = FakePersonalDataExportDao()
        val sut = CommonDataExportDataSource(client, dao, files)
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
        val result = fixture.sut.observe().first()

        then()
        assertNull(result)
    }

    @Test
    fun `saved never requested status is distinct from a missing row`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }

        whenn()
        fixture.sut.saveInDb(DataExport(DataExportStatus.NONE))

        then()
        assertEquals(DataExport(DataExportStatus.NONE), fixture.sut.observe().first())
        assertEquals("NONE", fixture.dao.export?.status)
    }

    @Test
    fun `restores status and all timestamps after datasource recreation`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveInDb(fixture.ready)
        val recreated = CommonDataExportDataSource(fixture.client, fixture.dao, fixture.files)

        whenn()
        val result = recreated.observe().first()

        then()
        assertEquals(fixture.ready, result)
    }

    @Test
    fun `saving replaces the singleton status and notifies active observers`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val running = DataExport(DataExportStatus.RUNNING)
        fixture.sut.saveInDb(running)
        val emissions = mutableListOf<DataExport?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.observe().collect { emissions.add(it) }
        }

        whenn()
        fixture.sut.saveInDb(fixture.ready)

        then()
        assertEquals(listOf<DataExport?>(running, fixture.ready), emissions)
        assertEquals(0, fixture.dao.export?.id)
    }

    @Test
    fun `publishes an unchanged saved status so screen recovery can complete`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val status = DataExport(DataExportStatus.NONE)
        fixture.sut.saveInDb(status)
        val emissions = mutableListOf<DataExport?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.observe().collect { emissions.add(it) }
        }

        whenn()
        fixture.sut.saveInDb(status)

        then()
        assertEquals(listOf<DataExport?>(status, status), emissions)
    }

    @Test
    fun `logout clears the saved status and notifies active observers`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveInDb(fixture.ready)
        val emissions = mutableListOf<DataExport?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.observe().collect { emissions.add(it) }
        }

        whenn()
        fixture.sut.doOnLogOut()

        then()
        assertNull(fixture.dao.export)
        assertEquals(listOf(fixture.ready, null), emissions)
        val recreated = CommonDataExportDataSource(fixture.client, fixture.dao, fixture.files)
        assertNull(recreated.observe().first())
    }

    @Test
    fun `saved file is found after datasource recreation`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val content = "{\"name\":\"Zażółć Україна\"}"
        val saved = fixture.sut.saveFile(fixture.ready, content)
        val recreated = CommonDataExportDataSource(fixture.client, fixture.dao, fixture.files)

        whenn()
        val result = recreated.getFile(fixture.ready)

        then()
        assertEquals(saved, result)
        assertEquals(content, fixture.files.files[saved])
    }

    @Test
    fun `stores the export as a named JSON file keyed by request and expiry`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }

        whenn()
        val result = fixture.sut.saveFile(fixture.ready, "{\"records\":[]}")

        then()
        val key = "${fixture.ready.requestedAt?.toEpochMilliseconds()}-" +
            "${fixture.ready.expiresAt?.toEpochMilliseconds()}"
        val expected = fixture.files.touchCacheFile("personal_data_exports") / key /
            "bookk-personal-data.json"
        assertEquals(expected, result)
        assertEquals("{\"records\":[]}", fixture.files.files[expected])
    }

    @Test
    fun `saving a new export file removes the previous one`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        val previous = fixture.ready.copy(requestedAt = null)
        fixture.sut.saveFile(previous, "{\"previous\":true}")

        whenn()
        fixture.sut.saveFile(fixture.ready, "{}")

        then()
        assertNull(fixture.sut.getFile(previous))
        assertNotNull(fixture.sut.getFile(fixture.ready))
    }

    @Test
    fun `missing export file is a cache miss`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }

        whenn()
        val result = fixture.sut.getFile(fixture.ready)

        then()
        assertNull(result)
    }

    @Test
    fun `different request or expiry does not reuse a stored file`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveFile(fixture.ready, "{}")

        whenn()
        val results = listOf(
            fixture.ready.copy(requestedAt = null),
            fixture.ready.copy(expiresAt = null),
        ).map { fixture.sut.getFile(it) }

        then()
        assertEquals(listOf(null, null), results)
    }

    @Test
    fun `cleanup removes the saved personal export file`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveFile(fixture.ready, "{}")

        whenn()
        fixture.sut.deleteFiles()

        then()
        assertNull(fixture.sut.getFile(fixture.ready))
    }

    @Test
    fun `cleanup preserves files outside the personal export directory`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveFile(fixture.ready, "{}")
        val businessFile = fixture.files.touchCacheFile("business_data_exports") / "export.json"
        fixture.files.writeText(businessFile, "{\"business\":true}")

        whenn()
        fixture.sut.deleteFiles()

        then()
        assertNull(fixture.sut.getFile(fixture.ready))
        assertEquals("{\"business\":true}", fixture.files.files[businessFile])
    }

    @Test
    fun `personal export can be cached again after cleanup`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveFile(fixture.ready, "{\"previous\":true}")
        fixture.sut.deleteFiles()

        whenn()
        fixture.sut.saveFile(fixture.ready, "{}")

        then()
        assertNotNull(fixture.sut.getFile(fixture.ready))
    }

    @Test
    fun `logout removes the downloaded file`() = runUnitTest {
        given()
        val fixture = Fixture().also { resources.add(it) }
        fixture.sut.saveFile(fixture.ready, "{}")

        whenn()
        fixture.sut.doOnLogOut()

        then()
        assertNull(fixture.sut.getFile(fixture.ready))
    }
}
