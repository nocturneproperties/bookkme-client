package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import me.bookk.feature.settings.domain.datasource.NotificationSettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UpdateNotificationSettingsImplTest {

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
        val sut = UpdateNotificationSettingsImpl(dataSource)
    }

    @Test
    fun `returns updated settings`() = runUnitTest {
        given()
        val fixture = Fixture()
        val settings = NotificationSettings.stub()
        val updated = NotificationSettings.stub()
        everySuspend { fixture.dataSource.updateNotificationSettings(settings) } returns updated
        everySuspend { fixture.dataSource.saveNotificationSettingsInDB(updated) } returns Unit

        whenn()
        val result = fixture.sut(settings)

        then()
        assertEquals(updated, result)
    }

    @Test
    fun `saves updated settings in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val settings = NotificationSettings.stub()
        val updated = NotificationSettings.stub()
        everySuspend { fixture.dataSource.updateNotificationSettings(settings) } returns updated
        everySuspend { fixture.dataSource.saveNotificationSettingsInDB(updated) } returns Unit

        whenn()
        fixture.sut(settings)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveNotificationSettingsInDB(updated) }
    }
}
