package me.bookk.feature.authorization.domain.impl

import dev.mokkery.MockMode
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class InitialAppDataFetchImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val now = Instant.fromEpochMilliseconds(1_700_000_000_000)

    private class Fixture {
        val userProfileCRUD = mock<UserProfileCRUD>()
        val refreshBusiness = mock<RefreshBusinessInfo>()
        val lowPriorityDataFetch = mock<LowPriorityDataFetch>(MockMode.autofill)
        val authorizationDataSource = mock<AuthorizationDataSource>(MockMode.autofill)
        val clock = mock<Clock>()
        val sut = InitialAppDataFetchImpl(userProfileCRUD, refreshBusiness, lowPriorityDataFetch, authorizationDataSource, clock)
    }

    private fun Fixture.stubFetchDue(lastFetchAt: Instant? = null) {
        everySuspend { authorizationDataSource.getLastInitialDataFetchAt() } returns lastFetchAt
        every { clock.now() } returns now
    }

    // timestampProtectedFetch()

    @Test
    fun `timestampProtectedFetch calls updateFromRemote on user profile`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } returns Unit

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
    }

    @Test
    fun `timestampProtectedFetch does not apply the dashboard id from remote`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } returns Unit

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) }
    }

    @Test
    fun `timestampProtectedFetch completes even when refreshBusiness throws`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } throws RuntimeException("network error")

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
    }

    @Test
    fun `timestampProtectedFetch triggers low priority data fetch`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } returns Unit

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verify { fixture.lowPriorityDataFetch() }
    }

    @Test
    fun `timestampProtectedFetch triggers low priority data fetch even when refreshBusiness throws`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } throws RuntimeException("network error")

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verify { fixture.lowPriorityDataFetch() }
    }

    @Test
    fun `timestampProtectedFetch fetches when there is no previous fetch timestamp`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue(lastFetchAt = null)
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } returns Unit

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
    }

    @Test
    fun `timestampProtectedFetch fetches when the last fetch was more than 30 minutes ago`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue(lastFetchAt = now - 31.minutes)
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } returns Unit

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
    }

    @Test
    fun `timestampProtectedFetch skips the fetch when the last fetch was less than 30 minutes ago`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue(lastFetchAt = now - 10.minutes)

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.userProfileCRUD.updateFromRemote() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.refreshBusiness(any()) }
        verify(VerifyMode.exactly(0)) { fixture.lowPriorityDataFetch() }
    }

    @Test
    fun `timestampProtectedFetch does not save the last fetch timestamp when the fetch is skipped`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue(lastFetchAt = now - 10.minutes)

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.authorizationDataSource.saveLastInitialDataFetchAt() }
    }

    @Test
    fun `timestampProtectedFetch saves the last fetch timestamp after a completed fetch`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } returns Unit

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend { fixture.authorizationDataSource.saveLastInitialDataFetchAt() }
    }

    // rawFetch()

    @Test
    fun `rawFetch calls updateFromRemote on user profile`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = true) } returns Unit

        whenn()
        fixture.sut.rawFetch()

        then()
        verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
    }

    @Test
    fun `rawFetch applies the dashboard id from remote`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = true) } returns Unit

        whenn()
        fixture.sut.rawFetch()

        then()
        verifySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = true) }
    }

    @Test
    fun `rawFetch completes even when refreshBusiness throws`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = true) } throws RuntimeException("network error")

        whenn()
        fixture.sut.rawFetch()

        then()
        verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
    }

    @Test
    fun `rawFetch triggers low priority data fetch`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = true) } returns Unit

        whenn()
        fixture.sut.rawFetch()

        then()
        verify { fixture.lowPriorityDataFetch() }
    }

    @Test
    fun `rawFetch runs even when the last fetch was less than 30 minutes ago`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue(lastFetchAt = now - 10.minutes)
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = true) } returns Unit

        whenn()
        fixture.sut.rawFetch()

        then()
        verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
    }

    @Test
    fun `rawFetch saves the last fetch timestamp after a completed fetch`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = true) } returns Unit

        whenn()
        fixture.sut.rawFetch()

        then()
        verifySuspend { fixture.authorizationDataSource.saveLastInitialDataFetchAt() }
    }
}
