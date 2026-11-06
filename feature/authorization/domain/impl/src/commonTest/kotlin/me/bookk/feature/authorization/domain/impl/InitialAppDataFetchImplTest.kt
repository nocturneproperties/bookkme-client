package me.bookk.feature.authorization.domain.impl

import dev.mokkery.MockMode
import dev.mokkery.answering.calls
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.GetSignUpLegalDocuments
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocuments
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.settings.domain.api.DeleteExpiredPersonalDataExportFile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
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
        val deleteExpiredPersonalDataExportFile = mock<DeleteExpiredPersonalDataExportFile> {
            everySuspend { invoke() } returns Unit
        }
        val getSignUpLegalDocuments = mock<GetSignUpLegalDocuments> {
            everySuspend { refresh() } returns SignUpLegalDocuments(
                SignUpLegalDocument(
                    SignUpLegalDocument.Document.PRIVACY_POLICY,
                    "privacy-v3",
                    "https://example.com/privacy",
                ),
                SignUpLegalDocument(
                    SignUpLegalDocument.Document.TERMS_OF_SERVICE,
                    "terms-v2",
                    "https://example.com/terms",
                ),
            )
        }

        init {
            everySuspend { authorizationDataSource.getAccessToken() } returns "token"
            every { authorizationDataSource.getIsAuthorizedFlow() } returns flowOf(true)
        }
        val sut = InitialAppDataFetchImpl(
            userProfileCRUD,
            refreshBusiness,
            lowPriorityDataFetch,
            authorizationDataSource,
            clock,
            getSignUpLegalDocuments,
            deleteExpiredPersonalDataExportFile,
        )
    }

    private fun Fixture.stubFetchDue(lastFetchAt: Instant? = null) {
        everySuspend { authorizationDataSource.getLastInitialDataFetchAt() } returns lastFetchAt
        every { clock.now() } returns now
    }

    // timestampProtectedFetch()

    @Test
    fun `does not fetch private data when unauthorized`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()
        everySuspend { fixture.authorizationDataSource.getAccessToken() } returns null
        every { fixture.authorizationDataSource.getIsAuthorizedFlow() } returns flowOf(false)
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } returns Unit

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend { fixture.getSignUpLegalDocuments.refresh() }
        verifySuspend(VerifyMode.not) { fixture.userProfileCRUD.updateFromRemote() }
        verifySuspend(VerifyMode.not) { fixture.refreshBusiness(any()) }
        verify(VerifyMode.not) { fixture.lowPriorityDataFetch() }
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.saveLastInitialDataFetchAt()
        }
    }

    @Test
    fun `tokens do not permit private fetching before authorization completes`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()
        every { fixture.authorizationDataSource.getIsAuthorizedFlow() } returns flowOf(false)
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } returns Unit

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend { fixture.getSignUpLegalDocuments.refresh() }
        verifySuspend(VerifyMode.not) { fixture.userProfileCRUD.updateFromRemote() }
        verifySuspend(VerifyMode.not) { fixture.refreshBusiness(any()) }
        verify(VerifyMode.not) { fixture.lowPriorityDataFetch() }
    }

    @Test
    fun `preloads legal documents before fetching the user profile`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()
        val events = mutableListOf<String>()
        everySuspend { fixture.getSignUpLegalDocuments.refresh() } calls {
            events.add("legal")
            SignUpLegalDocuments(
                SignUpLegalDocument(SignUpLegalDocument.Document.PRIVACY_POLICY, "p", "privacy"),
                SignUpLegalDocument(SignUpLegalDocument.Document.TERMS_OF_SERVICE, "t", "terms"),
            )
        }
        everySuspend { fixture.userProfileCRUD.updateFromRemote() } calls {
            events.add("profile")
            Unit
        }
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } returns Unit

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        assertEquals(listOf("legal", "profile"), events)
    }

    @Test
    fun `does not fetch private data or save a timestamp when legal preload fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()
        everySuspend { fixture.getSignUpLegalDocuments.refresh() } throws
            IllegalStateException("offline")

        whenn()
        val result = runCatching { fixture.sut.timestampProtectedFetch() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
        verifySuspend(VerifyMode.not) { fixture.userProfileCRUD.updateFromRemote() }
        verifySuspend(VerifyMode.not) { fixture.refreshBusiness(any()) }
        verify(VerifyMode.not) { fixture.lowPriorityDataFetch() }
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.saveLastInitialDataFetchAt()
        }
    }

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
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } throws
            RuntimeException("network error")

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
    fun `timestampProtectedFetch triggers low priority data fetch even when refreshBusiness throws`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue()
            everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
            everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } throws
                RuntimeException("network error")

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verify { fixture.lowPriorityDataFetch() }
        }

    @Test
    fun `timestampProtectedFetch fetches when there is no previous fetch timestamp`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue(lastFetchAt = null)
            everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
            everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } returns
                Unit

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
        }

    @Test
    fun `timestampProtectedFetch fetches when the last fetch was more than 30 minutes ago`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue(lastFetchAt = now - 31.minutes)
            everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
            everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } returns
                Unit

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
        }

    @Test
    fun `timestampProtectedFetch skips the fetch when the last fetch was less than 30 minutes ago`() =
        runUnitTest {
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
    fun `timestampProtectedFetch does not save the last fetch timestamp when the fetch is skipped`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue(lastFetchAt = now - 10.minutes)

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend(VerifyMode.exactly(0)) {
                fixture.authorizationDataSource.saveLastInitialDataFetchAt()
            }
        }

    @Test
    fun `timestampProtectedFetch saves the last fetch timestamp after a completed fetch`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue()
            everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
            everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } returns
                Unit

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend { fixture.authorizationDataSource.saveLastInitialDataFetchAt() }
        }

    @Test
    fun `timestampProtectedFetch deletes an expired personal data export file on every start`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue(lastFetchAt = now - 10.minutes)
            every { fixture.authorizationDataSource.getIsAuthorizedFlow() } returns flowOf(false)

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend(VerifyMode.exactly(1)) { fixture.deleteExpiredPersonalDataExportFile() }
        }

    @Test
    fun `timestampProtectedFetch still fetches when deleting the export file fails`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue()
            everySuspend { fixture.deleteExpiredPersonalDataExportFile() } throws
                IllegalStateException("disk")
            everySuspend { fixture.userProfileCRUD.updateFromRemote() } returns Unit
            everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = false) } returns
                Unit

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
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
        everySuspend { fixture.refreshBusiness(applyDashboardIdFromRemote = true) } throws
            RuntimeException("network error")

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
