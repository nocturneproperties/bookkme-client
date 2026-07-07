package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
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
import me.bookk.feature.authorization.domain.entity.UserProfile
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import me.bookk.feature.settings.domain.datasource.NotificationSettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun `returns cached settings when available in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val userId = Uuid.random()
        val cached = NotificationSettings.stub(userId)
        everySuspend { fixture.profileCRUD.get() } returns profile(userId)
        everySuspend { fixture.dataSource.getNotificationSettingsFromDB(userId) } returns cached

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(cached, result)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getNotificationSettings() }
    }

    @Test
    fun `fetches from remote and saves when DB returns null`() = runUnitTest {
        given()
        val fixture = Fixture()
        val userId = Uuid.random()
        val remote = NotificationSettings.stub(userId)
        everySuspend { fixture.profileCRUD.get() } returns profile(userId)
        everySuspend { fixture.dataSource.getNotificationSettingsFromDB(userId) } returns null
        everySuspend { fixture.dataSource.getNotificationSettings() } returns remote
        everySuspend { fixture.dataSource.saveNotificationSettingsInDB(remote) } returns Unit

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(remote, result)
        verifySuspend { fixture.dataSource.saveNotificationSettingsInDB(remote) }
    }

    @Test
    fun `cached calls onResultAvailable with DB value then remote value`() = runUnitTest {
        given()
        val fixture = Fixture()
        val userId = Uuid.random()
        val cachedSettings = NotificationSettings.stub(userId)
        val remoteSettings = NotificationSettings.stub(userId)
        everySuspend { fixture.profileCRUD.get() } returns profile(userId)
        everySuspend { fixture.dataSource.getNotificationSettingsFromDB(userId) } returns cachedSettings
        everySuspend { fixture.dataSource.getNotificationSettings() } returns remoteSettings
        everySuspend { fixture.dataSource.saveNotificationSettingsInDB(remoteSettings) } returns Unit
        val received = mutableListOf<NotificationSettings>()

        whenn()
        fixture.sut.cached { received.add(it) }

        then()
        assertEquals(listOf(cachedSettings, remoteSettings), received)
    }

    @Test
    fun `cached skips DB callback when DB is null`() = runUnitTest {
        given()
        val fixture = Fixture()
        val userId = Uuid.random()
        val remoteSettings = NotificationSettings.stub(userId)
        everySuspend { fixture.profileCRUD.get() } returns profile(userId)
        everySuspend { fixture.dataSource.getNotificationSettingsFromDB(userId) } returns null
        everySuspend { fixture.dataSource.getNotificationSettings() } returns remoteSettings
        everySuspend { fixture.dataSource.saveNotificationSettingsInDB(remoteSettings) } returns Unit
        val received = mutableListOf<NotificationSettings>()

        whenn()
        fixture.sut.cached { received.add(it) }

        then()
        assertEquals(listOf(remoteSettings), received)
    }
}
