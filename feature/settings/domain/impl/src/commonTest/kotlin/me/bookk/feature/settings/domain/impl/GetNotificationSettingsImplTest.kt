package me.bookk.feature.settings.domain.impl

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.UserProfile
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import me.bookk.feature.settings.domain.datasource.NotificationSettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetNotificationSettingsImplTest {

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
        val dataSource = mock<NotificationSettingsDataSource>()
        val profileCRUD = mock<UserProfileCRUD>()
        val sut = GetNotificationSettingsImpl(dataSource, profileCRUD)
    }

    private fun profile(userId: Uuid) = UserProfile(
        id = userId,
        firstName = "First",
        lastName = "Last",
        email = "user@example.com"
    )

    @Test
    fun `flow emits the db value for the stored user`() = runUnitTest {
        given()
        val fixture = Fixture()
        val userId = Uuid.random()
        val settings = NotificationSettings.stub(userId)
        every { fixture.profileCRUD.observe() } returns flowOf(profile(userId))
        every { fixture.dataSource.observeNotificationSettingsDBChanges(userId) } returns flowOf(settings)

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(settings, result)
    }

    @Test
    fun `flow emits null when no settings are saved yet`() = runUnitTest {
        given()
        val fixture = Fixture()
        val userId = Uuid.random()
        every { fixture.profileCRUD.observe() } returns flowOf(profile(userId))
        every { fixture.dataSource.observeNotificationSettingsDBChanges(userId) } returns flowOf(null)

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertNull(result)
    }

    @Test
    fun `flow emits null without touching the network when no profile is stored`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.profileCRUD.observe() } returns flowOf(null)

        whenn()
        val result = fixture.sut.flow().toList()

        then()
        assertEquals(listOf<NotificationSettings?>(null), result)
        verifySuspend(VerifyMode.exactly(0)) { fixture.profileCRUD.get() }
    }

    @Test
    fun `flow switches to the user settings once a profile is stored`() = runUnitTest {
        given()
        val fixture = Fixture()
        val userId = Uuid.random()
        val settings = NotificationSettings.stub(userId)
        every { fixture.profileCRUD.observe() } returns flowOf(null, profile(userId))
        every { fixture.dataSource.observeNotificationSettingsDBChanges(userId) } returns flowOf(settings)

        whenn()
        val result = fixture.sut.flow().toList()

        then()
        assertEquals(listOf(null, settings), result)
    }

    @Test
    fun `flow never fetches from remote`() = runUnitTest {
        given()
        val fixture = Fixture()
        val userId = Uuid.random()
        every { fixture.profileCRUD.observe() } returns flowOf(profile(userId))
        every { fixture.dataSource.observeNotificationSettingsDBChanges(userId) } returns flowOf(null)

        whenn()
        fixture.sut.flow().first()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getNotificationSettings() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.profileCRUD.get() }
    }

    @Test
    fun `refresh fetches from remote and saves in db`() = runUnitTest {
        given()
        val fixture = Fixture()
        val userId = Uuid.random()
        val remote = NotificationSettings.stub(userId)
        everySuspend { fixture.profileCRUD.get() } returns profile(userId)
        everySuspend { fixture.dataSource.getNotificationSettings() } returns remote
        everySuspend { fixture.dataSource.saveNotificationSettingsInDB(remote) } returns Unit

        whenn()
        val result = fixture.sut.refresh()

        then()
        assertEquals(remote, result)
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveNotificationSettingsInDB(remote) }
    }

    @Test
    fun `refresh ensures the profile is stored before fetching settings`() = runUnitTest {
        given()
        val fixture = Fixture()
        val userId = Uuid.random()
        val remote = NotificationSettings.stub(userId)
        everySuspend { fixture.profileCRUD.get() } returns profile(userId)
        everySuspend { fixture.dataSource.getNotificationSettings() } returns remote
        everySuspend { fixture.dataSource.saveNotificationSettingsInDB(remote) } returns Unit

        whenn()
        fixture.sut.refresh()

        then()
        verifySuspend(VerifyMode.order) {
            fixture.profileCRUD.get()
            fixture.dataSource.getNotificationSettings()
        }
    }

    @Test
    fun `refresh propagates a profile fetch error without fetching settings`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.profileCRUD.get() } throws IllegalStateException()

        whenn()
        val error = runCatching { fixture.sut.refresh() }.exceptionOrNull()

        then()
        assertFailsWith<IllegalStateException> { throw error!! }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getNotificationSettings() }
    }

    @Test
    fun `refresh propagates a fetch error without saving`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.profileCRUD.get() } returns profile(Uuid.random())
        everySuspend { fixture.dataSource.getNotificationSettings() } throws IllegalStateException()

        whenn()
        val error = runCatching { fixture.sut.refresh() }.exceptionOrNull()

        then()
        assertFailsWith<IllegalStateException> { throw error!! }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveNotificationSettingsInDB(any()) }
    }
}
