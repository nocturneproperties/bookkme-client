package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.datasource.deletion.AccountDeletionDataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant

class GetAccountDeletionImplTest {
    private class Fixture {
        val dataSource = mock<AccountDeletionDataSource> {
            everySuspend { saveScheduledDeletion(any()) } returns Unit
        }
        val sut = GetAccountDeletionImpl(dataSource)
    }

    @Test
    fun `emits the scheduled deletion kept on the device`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.dataSource.observeScheduledDeletion() } returns flowOf(DELETES_AT)

        whenn()
        val deletesAt = fixture.sut.flow().first()

        then()
        assertEquals(DELETES_AT, deletesAt)
    }

    @Test
    fun `keeps the scheduled deletion the server reports`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getScheduledDeletion() } returns DELETES_AT

        whenn()
        fixture.sut.refresh()

        then()
        verifySuspend { fixture.dataSource.saveScheduledDeletion(DELETES_AT) }
    }

    @Test
    fun `forgets a deletion the server no longer has`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getScheduledDeletion() } returns null

        whenn()
        fixture.sut.refresh()

        then()
        verifySuspend { fixture.dataSource.saveScheduledDeletion(null) }
    }

    @Test
    fun `keeps the cached deletion when the server can't be reached`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getScheduledDeletion() } throws IllegalStateException()

        whenn()
        val result = runCatching { fixture.sut.refresh() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
        verifySuspend(VerifyMode.not) { fixture.dataSource.saveScheduledDeletion(any()) }
    }

    private companion object {
        val DELETES_AT = Instant.parse("2026-12-03T14:00:00Z")
    }
}
