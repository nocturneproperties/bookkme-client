package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.datasource.deletion.AccountDeletionDataSource
import kotlin.test.Test
import kotlin.test.assertSame

class CancelAccountDeletionImplTest {
    private class Fixture {
        val dataSource = mock<AccountDeletionDataSource> {
            everySuspend { cancelScheduledDeletion() } returns Unit
            everySuspend { saveScheduledDeletion(any()) } returns Unit
        }
        val sut = CancelAccountDeletionImpl(dataSource)
    }

    @Test
    fun `cancels the deletion on the server`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.dataSource.cancelScheduledDeletion() }
    }

    @Test
    fun `forgets the scheduled deletion once the server cancelled it`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.order) {
            fixture.dataSource.cancelScheduledDeletion()
            fixture.dataSource.saveScheduledDeletion(null)
        }
    }

    @Test
    fun `keeps the scheduled deletion when the server can't cancel it`() = runUnitTest {
        given()
        val fixture = Fixture()
        val failure = IllegalStateException("offline")
        everySuspend { fixture.dataSource.cancelScheduledDeletion() } throws failure

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertSame(failure, result.exceptionOrNull())
        verifySuspend(VerifyMode.not) { fixture.dataSource.saveScheduledDeletion(any()) }
    }
}
