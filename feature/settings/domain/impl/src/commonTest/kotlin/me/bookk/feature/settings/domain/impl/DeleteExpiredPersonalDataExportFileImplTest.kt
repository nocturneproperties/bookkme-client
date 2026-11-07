package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteExpiredPersonalDataExportFileImplTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture(
        cached: DataExport?,
    ) {
        val now = Instant.parse("2026-11-05T12:00:00Z")
        val dataSource = mock<DataExportDataSource> {
            every { observe() } returns flowOf(cached)
            everySuspend { deleteFiles() } returns Unit
        }
        val sut = DeleteExpiredPersonalDataExportFileImpl(
            dataSource,
            object : Clock {
                override fun now(): Instant {
                    return now
                }
            },
        )
    }

    @Test
    fun `keeps the file of a ready export that has not expired`() = runUnitTest {
        given()
        val expiresAt = Instant.parse("2026-11-05T12:00:01Z")
        val fixture = Fixture(DataExport(DataExportStatus.READY, expiresAt = expiresAt))

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.not) { fixture.dataSource.deleteFiles() }
    }

    @Test
    fun `keeps the file of a ready export without a known expiry`() = runUnitTest {
        given()
        val fixture = Fixture(DataExport(DataExportStatus.READY))

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.not) { fixture.dataSource.deleteFiles() }
    }

    @Test
    fun `deletes the file of a ready export at or after its expiry`() = runUnitTest {
        given()
        val now = Instant.parse("2026-11-05T12:00:00Z")
        val fixtures = listOf(now, now - 1.seconds, now - 1.days).map {
            Fixture(DataExport(DataExportStatus.READY, expiresAt = it))
        }

        whenn()
        fixtures.forEach { it.sut() }

        then()
        fixtures.forEach { verifySuspend(VerifyMode.exactly(1)) { it.dataSource.deleteFiles() } }
    }

    @Test
    fun `deletes files when the cached export is not ready`() = runUnitTest {
        given()
        val fixtures = listOf(
            DataExportStatus.NONE,
            DataExportStatus.RUNNING,
            DataExportStatus.FAILED,
        ).map { Fixture(DataExport(it)) }

        whenn()
        fixtures.forEach { it.sut() }

        then()
        fixtures.forEach { verifySuspend(VerifyMode.exactly(1)) { it.dataSource.deleteFiles() } }
    }

    @Test
    fun `deletes files when no export is cached`() = runUnitTest {
        given()
        val fixture = Fixture(null)

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.deleteFiles() }
    }
}
