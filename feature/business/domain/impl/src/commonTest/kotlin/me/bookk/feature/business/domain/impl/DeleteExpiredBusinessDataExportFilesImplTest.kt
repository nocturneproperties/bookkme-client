package me.bookk.feature.business.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteExpiredBusinessDataExportFilesImplTest {
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
        cached: Map<Uuid, DataExport>,
    ) {
        val dataSource = mock<DataExportDataSource> {
            everySuspend { getAllFromDb() } returns cached
            everySuspend { deleteFiles(any()) } returns Unit
        }
        val sut = DeleteExpiredBusinessDataExportFilesImpl(
            dataSource,
            object : Clock {
                override fun now(): Instant {
                    return NOW
                }
            },
        )
    }

    @Test
    fun `keeps files of ready exports that have not expired or have no known expiry`() =
        runUnitTest {
            given()
            val fixture = Fixture(
                mapOf(
                    FIRST to DataExport(DataExportStatus.READY, expiresAt = NOW + 1.seconds),
                    SECOND to DataExport(DataExportStatus.READY),
                ),
            )

            whenn()
            fixture.sut()

            then()
            verifySuspend(VerifyMode.not) { fixture.dataSource.deleteFiles(any()) }
        }

    @Test
    fun `deletes files of ready exports at or after their expiry`() = runUnitTest {
        given()
        val fixtures = listOf(NOW, NOW - 1.seconds, NOW - 1.days).map {
            Fixture(mapOf(FIRST to DataExport(DataExportStatus.READY, expiresAt = it)))
        }

        whenn()
        fixtures.forEach { it.sut() }

        then()
        fixtures.forEach {
            verifySuspend(VerifyMode.exactly(1)) { it.dataSource.deleteFiles(FIRST) }
        }
    }

    @Test
    fun `deletes files of exports that are not ready`() = runUnitTest {
        given()
        val fixtures = listOf(
            DataExportStatus.NONE,
            DataExportStatus.RUNNING,
            DataExportStatus.FAILED,
        ).map { Fixture(mapOf(FIRST to DataExport(it))) }

        whenn()
        fixtures.forEach { it.sut() }

        then()
        fixtures.forEach {
            verifySuspend(VerifyMode.exactly(1)) { it.dataSource.deleteFiles(FIRST) }
        }
    }

    @Test
    fun `cleans each business independently`() = runUnitTest {
        given()
        val fixture = Fixture(
            mapOf(
                FIRST to DataExport(DataExportStatus.READY, expiresAt = NOW - 1.days),
                SECOND to DataExport(DataExportStatus.READY, expiresAt = NOW + 1.days),
            ),
        )

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.deleteFiles(FIRST) }
        verifySuspend(VerifyMode.not) { fixture.dataSource.deleteFiles(SECOND) }
    }

    @Test
    fun `propagates cleanup failures`() = runUnitTest {
        given()
        val fixture = Fixture(mapOf(FIRST to DataExport(DataExportStatus.NONE)))
        everySuspend { fixture.dataSource.deleteFiles(FIRST) } throws IllegalStateException("disk")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }

    private companion object {
        val NOW = Instant.parse("2026-11-05T12:00:00Z")
        val FIRST = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val SECOND = Uuid.parse("00000000-0000-0000-0000-000000000002")
    }
}
