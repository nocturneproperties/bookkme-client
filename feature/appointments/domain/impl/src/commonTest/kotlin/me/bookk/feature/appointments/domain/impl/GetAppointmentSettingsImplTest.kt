package me.bookk.feature.appointments.domain.impl

import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.domain.datasource.AppointmentSettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetAppointmentSettingsImplTest {

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
        val dataSource = mockk<AppointmentSettingsDataSource>()
        val sut = GetAppointmentSettingsImpl(dataSource)
    }

    @Test
    fun `returns cached settings when available in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val cached = AppointmentSettings.stub(businessId)
        coEvery { fixture.dataSource.getAppointmentSettingsFromDB(businessId) } returns cached

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(cached, result)
        coVerify(exactly = 0) { fixture.dataSource.getAppointmentSettings(any()) }
    }

    @Test
    fun `fetches from remote and saves when DB returns null`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val remote = AppointmentSettings.stub(businessId)
        coEvery { fixture.dataSource.getAppointmentSettingsFromDB(businessId) } returns null
        coEvery { fixture.dataSource.getAppointmentSettings(businessId) } returns remote
        coJustRun { fixture.dataSource.saveAppointmentSettingsInDB(remote) }

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(remote, result)
        coVerify { fixture.dataSource.saveAppointmentSettingsInDB(remote) }
    }

    @Test
    fun `cached() calls onResultAvailable with DB value then remote value`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val cachedSettings = AppointmentSettings.stub(businessId)
        val remoteSettings = AppointmentSettings.stub(businessId)
        coEvery { fixture.dataSource.getAppointmentSettingsFromDB(businessId) } returns cachedSettings
        coEvery { fixture.dataSource.getAppointmentSettings(businessId) } returns remoteSettings
        coJustRun { fixture.dataSource.saveAppointmentSettingsInDB(remoteSettings) }
        val received = mutableListOf<AppointmentSettings>()

        whenn()
        fixture.fixture.cached(businessId) { received.add(it) }

        then()
        assertEquals(listOf(cachedSettings, remoteSettings), received)
    }

    @Test
    fun `cached() skips DB callback when DB is null`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val remoteSettings = AppointmentSettings.stub(businessId)
        coEvery { fixture.dataSource.getAppointmentSettingsFromDB(businessId) } returns null
        coEvery { fixture.dataSource.getAppointmentSettings(businessId) } returns remoteSettings
        coJustRun { fixture.dataSource.saveAppointmentSettingsInDB(remoteSettings) }
        val received = mutableListOf<AppointmentSettings>()

        whenn()
        fixture.fixture.cached(businessId) { received.add(it) }

        then()
        assertEquals(listOf(remoteSettings), received)
    }
}
