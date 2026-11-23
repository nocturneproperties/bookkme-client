package me.bookk.feature.authorization.domain.impl

import dev.mokkery.MockMode
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.domain.fetch.AppStartAction
import me.bookk.core.domain.fetch.BackgroundDataFetchAction
import me.bookk.core.domain.fetch.DataFetchAction
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.GetSignUpLegalDocuments
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocuments
import me.bookk.feature.authorization.domain.entity.UserProfile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

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
        val userProfileCRUD = mock<UserProfileCRUD> {
            everySuspend { updateFromRemote() } returns Unit
            every { observe() } returns flowOf(
                UserProfile(
                    id = Uuid.parse("00000000-0000-0000-0000-000000000001"),
                    firstName = "Jane",
                    lastName = "Doe",
                    email = "jane@example.com",
                ),
            )
        }
        val authorizationDataSource = mock<AuthorizationDataSource>(MockMode.autofill)
        val clock = mock<Clock>()
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
        val firstAppStart = mock<AppStartAction> {
            everySuspend { doOnAppStart() } returns Unit
        }
        val secondAppStart = mock<AppStartAction> {
            everySuspend { doOnAppStart() } returns Unit
        }
        val firstDataFetch = mock<DataFetchAction> {
            everySuspend { doOnDataFetch(any()) } returns Unit
        }
        val secondDataFetch = mock<DataFetchAction> {
            everySuspend { doOnDataFetch(any()) } returns Unit
        }
        val firstBackgroundFetch = mock<BackgroundDataFetchAction> {
            everySuspend { doOnBackgroundDataFetch() } returns Unit
        }
        val secondBackgroundFetch = mock<BackgroundDataFetchAction> {
            everySuspend { doOnBackgroundDataFetch() } returns Unit
        }

        init {
            every { authorizationDataSource.getIsAuthorizedFlow() } returns flowOf(true)
            everySuspend { authorizationDataSource.isDatabasePersistent() } returns true
        }

        val sut = InitialAppDataFetchImpl(
            userProfileCRUD,
            authorizationDataSource,
            clock,
            getSignUpLegalDocuments,
            CoroutineScope(UnconfinedTestDispatcher()),
            listOf(firstAppStart, secondAppStart),
            listOf(firstDataFetch, secondDataFetch),
            listOf(firstBackgroundFetch, secondBackgroundFetch),
        )
    }

    private fun Fixture.stubFetchDue(lastFetchAt: Instant? = null) {
        everySuspend { authorizationDataSource.getLastInitialDataFetchAt() } returns lastFetchAt
        every { clock.now() } returns now
    }

    private fun Fixture.verifyNoPrivateFetch() {
        verifySuspend(VerifyMode.not) { userProfileCRUD.updateFromRemote() }
        verifySuspend(VerifyMode.not) { firstDataFetch.doOnDataFetch(any()) }
        verifySuspend(VerifyMode.not) { firstBackgroundFetch.doOnBackgroundDataFetch() }
    }

    @Test
    fun `does not fetch private data when unauthorized`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()
        every { fixture.authorizationDataSource.getIsAuthorizedFlow() } returns flowOf(false)

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend { fixture.getSignUpLegalDocuments.refresh() }
        fixture.verifyNoPrivateFetch()
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.saveLastInitialDataFetchAt()
        }
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
        fixture.verifyNoPrivateFetch()
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.saveLastInitialDataFetchAt()
        }
    }

    @Test
    fun `timestampProtectedFetch calls updateFromRemote on user profile`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
    }

    @Test
    fun `timestampProtectedFetch runs every data fetch as an app start`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend { fixture.firstDataFetch.doOnDataFetch(isSignIn = false) }
        verifySuspend { fixture.secondDataFetch.doOnDataFetch(isSignIn = false) }
    }

    @Test
    fun `timestampProtectedFetch still runs the remaining data fetches when one fails`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue()
            everySuspend { fixture.firstDataFetch.doOnDataFetch(any()) } throws
                RuntimeException("network error")

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend { fixture.secondDataFetch.doOnDataFetch(isSignIn = false) }
            verifySuspend { fixture.authorizationDataSource.saveLastInitialDataFetchAt() }
        }

    @Test
    fun `timestampProtectedFetch does not run data fetches when the profile update fails`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue()
            everySuspend { fixture.userProfileCRUD.updateFromRemote() } throws
                RuntimeException("network error")

            whenn()
            val result = runCatching { fixture.sut.timestampProtectedFetch() }

            then()
            assertFailsWith<RuntimeException> { result.getOrThrow() }
            verifySuspend(VerifyMode.not) { fixture.firstDataFetch.doOnDataFetch(any()) }
            verifySuspend(VerifyMode.not) {
                fixture.firstBackgroundFetch.doOnBackgroundDataFetch()
            }
            verifySuspend(VerifyMode.not) {
                fixture.authorizationDataSource.saveLastInitialDataFetchAt()
            }
        }

    @Test
    fun `timestampProtectedFetch runs every background data fetch`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend { fixture.firstBackgroundFetch.doOnBackgroundDataFetch() }
        verifySuspend { fixture.secondBackgroundFetch.doOnBackgroundDataFetch() }
    }

    @Test
    fun `timestampProtectedFetch starts background fetches after every data fetch`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()
        val events = mutableListOf<String>()
        everySuspend { fixture.secondDataFetch.doOnDataFetch(any()) } calls {
            events.add("data")
            Unit
        }
        everySuspend { fixture.firstBackgroundFetch.doOnBackgroundDataFetch() } calls {
            events.add("background")
            Unit
        }

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        assertEquals(listOf("data", "background"), events)
    }

    @Test
    fun `timestampProtectedFetch runs background fetches even when a data fetch fails`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue()
            everySuspend { fixture.firstDataFetch.doOnDataFetch(any()) } throws
                RuntimeException("network error")

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend { fixture.firstBackgroundFetch.doOnBackgroundDataFetch() }
        }

    @Test
    fun `timestampProtectedFetch still runs the remaining background fetches when one fails`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue()
            everySuspend { fixture.firstBackgroundFetch.doOnBackgroundDataFetch() } throws
                RuntimeException("network error")

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend { fixture.secondBackgroundFetch.doOnBackgroundDataFetch() }
        }

    @Test
    fun `timestampProtectedFetch fetches when there is no previous fetch timestamp`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue(lastFetchAt = null)

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
            fixture.verifyNoPrivateFetch()
        }

    @Test
    fun `timestampProtectedFetch fetches on every start while the database is not persistent`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue(lastFetchAt = now - 10.minutes)
            everySuspend { fixture.authorizationDataSource.isDatabasePersistent() } returns false

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
            verifySuspend { fixture.firstDataFetch.doOnDataFetch(false) }
        }

    @Test
    fun `timestampProtectedFetch fetches when the profile is missing although the last fetch was recent`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue(lastFetchAt = now - 10.minutes)
            every { fixture.userProfileCRUD.observe() } returns flowOf(null)

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
            verifySuspend { fixture.firstDataFetch.doOnDataFetch(false) }
        }

    @Test
    fun `timestampProtectedFetch skips the fetch for a missing profile while unauthorized`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue(lastFetchAt = now - 10.minutes)
            every { fixture.userProfileCRUD.observe() } returns flowOf(null)
            every { fixture.authorizationDataSource.getIsAuthorizedFlow() } returns flowOf(false)

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend(VerifyMode.not) { fixture.getSignUpLegalDocuments.refresh() }
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
            verifySuspend(VerifyMode.not) {
                fixture.authorizationDataSource.saveLastInitialDataFetchAt()
            }
        }

    @Test
    fun `timestampProtectedFetch saves the last fetch timestamp after a completed fetch`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue()

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend { fixture.authorizationDataSource.saveLastInitialDataFetchAt() }
        }

    @Test
    fun `timestampProtectedFetch runs every app start action even when the fetch is skipped`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubFetchDue(lastFetchAt = now - 10.minutes)
            every { fixture.authorizationDataSource.getIsAuthorizedFlow() } returns flowOf(false)

            whenn()
            fixture.sut.timestampProtectedFetch()

            then()
            verifySuspend(VerifyMode.exactly(1)) { fixture.firstAppStart.doOnAppStart() }
            verifySuspend(VerifyMode.exactly(1)) { fixture.secondAppStart.doOnAppStart() }
        }

    @Test
    fun `timestampProtectedFetch still fetches when an app start action fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue()
        everySuspend { fixture.firstAppStart.doOnAppStart() } throws IllegalStateException("disk")

        whenn()
        fixture.sut.timestampProtectedFetch()

        then()
        verifySuspend { fixture.secondAppStart.doOnAppStart() }
        verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
        verifySuspend { fixture.authorizationDataSource.saveLastInitialDataFetchAt() }
    }

    @Test
    fun `rawFetch calls updateFromRemote on user profile`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.rawFetch()

        then()
        verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
    }

    @Test
    fun `rawFetch runs every data fetch as a sign-in`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.rawFetch()

        then()
        verifySuspend { fixture.firstDataFetch.doOnDataFetch(isSignIn = true) }
        verifySuspend { fixture.secondDataFetch.doOnDataFetch(isSignIn = true) }
    }

    @Test
    fun `rawFetch completes even when a data fetch fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.firstDataFetch.doOnDataFetch(any()) } throws
            RuntimeException("network error")

        whenn()
        fixture.sut.rawFetch()

        then()
        verifySuspend { fixture.authorizationDataSource.saveLastInitialDataFetchAt() }
    }

    @Test
    fun `rawFetch runs every background data fetch`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.rawFetch()

        then()
        verifySuspend { fixture.firstBackgroundFetch.doOnBackgroundDataFetch() }
        verifySuspend { fixture.secondBackgroundFetch.doOnBackgroundDataFetch() }
    }

    @Test
    fun `rawFetch does not run app start actions`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.rawFetch()

        then()
        verifySuspend(VerifyMode.not) { fixture.firstAppStart.doOnAppStart() }
    }

    @Test
    fun `rawFetch runs even when the last fetch was less than 30 minutes ago`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubFetchDue(lastFetchAt = now - 10.minutes)

        whenn()
        fixture.sut.rawFetch()

        then()
        verifySuspend { fixture.userProfileCRUD.updateFromRemote() }
    }

    @Test
    fun `rawFetch saves the last fetch timestamp after a completed fetch`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut.rawFetch()

        then()
        verifySuspend { fixture.authorizationDataSource.saveLastInitialDataFetchAt() }
    }
}
