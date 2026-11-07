package me.bookk.feature.business.domain.impl

import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
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
import me.bookk.feature.business.domain.api.GetBusinessDataExport
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class GetBusinessDataExportImplTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture {
        val id = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val now = Instant.parse("2026-11-05T12:00:00Z")
        val cached = MutableStateFlow<DataExport?>(null)
        val dataSource = mock<DataExportDataSource> {
            every { observe(id) } returns cached
            everySuspend { saveInDb(id, any()) } calls { (_: Uuid, export: DataExport) ->
                cached.value = export
            }
            everySuspend { deleteFromDb(id) } calls { cached.value = null }
            everySuspend { deleteFiles(id) } returns Unit
        }
        val sut = GetBusinessDataExportImpl(
            dataSource,
            object : Clock {
                override fun now(): Instant {
                    return now
                }
            },
        )
    }

    @Test
    fun `emits null for exports at or after expiry without touching the cache`() = runUnitTest {
        given()
        val fixtures = DataExportStatus.entries.flatMap { status ->
            listOf(0.days, 1.days).map { elapsed ->
                Fixture().also {
                    it.cached.value = DataExport(status, expiresAt = it.now - elapsed)
                }
            }
        }

        whenn()
        val results = fixtures.map { it.sut.flow(it.id).first() }

        then()
        assertEquals(List(fixtures.size) { null }, results)
        fixtures.forEach {
            verifySuspend(VerifyMode.exactly(0)) { it.dataSource.deleteFiles(any()) }
            verifySuspend(VerifyMode.exactly(0)) { it.dataSource.saveInDb(any(), any()) }
        }
    }

    @Test
    fun `emits unexpired exports and exports without expiry unchanged`() = runUnitTest {
        given()
        val values = DataExportStatus.entries.flatMap { status ->
            listOf(
                DataExport(status),
                DataExport(status, expiresAt = Instant.parse("2026-11-06T12:00:00Z")),
            )
        }
        val fixtures = values.map { export -> Fixture().also { it.cached.value = export } }

        whenn()
        val results = fixtures.map { it.sut.flow(it.id).first() }

        then()
        assertEquals(values, results)
    }

    @Test
    fun `emits null once a later value is expired while observation continues`() = runUnitTest {
        given()
        val fixture = Fixture()
        val ready = DataExport(DataExportStatus.READY, expiresAt = fixture.now + 1.days)
        fixture.cached.value = ready
        val emissions = mutableListOf<DataExport?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.flow(fixture.id).collect { emissions.add(it) }
        }

        whenn()
        fixture.cached.value = ready.copy(expiresAt = fixture.now)
        fixture.cached.value = ready

        then()
        assertEquals(listOf(ready, null, ready), emissions)
    }

    @Test
    fun `refresh saves and returns the server export of the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val expected = DataExport(DataExportStatus.RUNNING)
        everySuspend { fixture.dataSource.get(fixture.id) } returns expected

        whenn()
        val result = fixture.sut.refresh(fixture.id)

        then()
        assertEquals(expected, result)
        assertEquals(expected, fixture.cached.value)
    }

    @Test
    fun `refresh keeps the saved file while the export stays downloadable`() = runUnitTest {
        given()
        val fixture = Fixture()
        val values = listOf(
            DataExport(DataExportStatus.READY),
            DataExport(DataExportStatus.READY, expiresAt = fixture.now + 1.days),
        )

        whenn()
        values.forEach {
            everySuspend { fixture.dataSource.get(fixture.id) } returns it
            fixture.sut.refresh(fixture.id)
        }

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteFiles(any()) }
    }

    @Test
    fun `refresh deletes the saved file when the export is no longer downloadable`() = runUnitTest {
        given()
        val now = Fixture().now
        val values = DataExportStatus.entries
            .filter { it != DataExportStatus.READY }
            .map { DataExport(it) } + DataExport(DataExportStatus.READY, expiresAt = now)
        val fixtures = values.map { export ->
            Fixture().also { everySuspend { it.dataSource.get(it.id) } returns export }
        }

        whenn()
        fixtures.forEach { it.sut.refresh(it.id) }

        then()
        fixtures.forEach {
            verifySuspend { it.dataSource.deleteFiles(it.id) }
        }
        assertEquals(values, fixtures.map { it.cached.value })
    }

    @Test
    fun `refresh forgets the cached export and file when the caller does not own the business`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.cached.value = DataExport(DataExportStatus.READY)
            everySuspend { fixture.dataSource.get(fixture.id) } throws
                DomainError.NotFound(IllegalStateException("404"))

            whenn()
            val result = runCatching { fixture.sut.refresh(fixture.id) }

            then()
            assertFailsWith<GetBusinessDataExport.Error.NotOwner> { result.getOrThrow() }
            assertEquals(null, fixture.cached.value)
            verifySuspend { fixture.dataSource.deleteFiles(fixture.id) }
        }

    @Test
    fun `failed refresh leaves the cached export and files intact`() = runUnitTest {
        given()
        val fixture = Fixture()
        val cached = DataExport(DataExportStatus.READY)
        fixture.cached.value = cached
        everySuspend { fixture.dataSource.get(fixture.id) } throws IllegalStateException("offline")

        whenn()
        val result = runCatching { fixture.sut.refresh(fixture.id) }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
        assertEquals(cached, fixture.cached.value)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteFiles(any()) }
    }

    @Test
    fun `refresh cleanup failure propagates without saving the new status`() = runUnitTest {
        given()
        val fixture = Fixture()
        val cached = DataExport(DataExportStatus.READY)
        fixture.cached.value = cached
        everySuspend { fixture.dataSource.get(fixture.id) } returns
            DataExport(DataExportStatus.NONE)
        everySuspend { fixture.dataSource.deleteFiles(fixture.id) } throws
            IllegalStateException("disk")

        whenn()
        val result = runCatching { fixture.sut.refresh(fixture.id) }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
        assertEquals(cached, fixture.cached.value)
    }
}
