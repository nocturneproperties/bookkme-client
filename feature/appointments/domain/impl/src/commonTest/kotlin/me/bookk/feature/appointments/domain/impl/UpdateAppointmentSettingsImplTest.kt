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
import me.bookk.feature.appointments.domain.api.UpdateAppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.domain.datasource.AppointmentSettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateAppointmentSettingsImplTest {

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
        val sut = UpdateAppointmentSettingsImpl(dataSource)
    }

    @Test
    fun `returns updated settings`() = runUnitTest {
        given()
        val fixture = Fixture()
        val settings = AppointmentSettings.stub()
        val updated = AppointmentSettings.stub()
        coEvery { fixture.dataSource.updateAppointmentSettings(settings) } returns updated
        coJustRun { fixture.dataSource.saveAppointmentSettingsInDB(updated) }

        whenn()
        val result = fixture.sut(settings)

        then()
        assertEquals(updated, result)
    }

    @Test
    fun `saves updated settings in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val settings = AppointmentSettings.stub()
        val updated = AppointmentSettings.stub()
        coEvery { fixture.dataSource.updateAppointmentSettings(settings) } returns updated
        coJustRun { fixture.dataSource.saveAppointmentSettingsInDB(updated) }

        whenn()
        fixture.sut(settings)

        then()
        coVerify(exactly = 1) { fixture.dataSource.saveAppointmentSettingsInDB(updated) }
    }

    @Test
    fun `throws ActiveDayWithoutWorkHours on corresponding error code`() = runUnitTest {
        given()
        val fixture = Fixture()
        val settings = AppointmentSettings.stub()
        coEvery { fixture.dataSource.updateAppointmentSettings(settings) } throws
            DomainError.BusinessError(AppointmentErrorCodes.ACTIVE_DAY_WITHOUT_WORK_HOURS, "msg")

        whenn()
        then()
        assertFailsWith<UpdateAppointmentSettings.Error.ActiveDayWithoutWorkHours> {
            fixture.sut(settings)
        }
    }

    @Test
    fun `throws InvalidDayOffRange on corresponding error code`() = runUnitTest {
        given()
        val fixture = Fixture()
        val settings = AppointmentSettings.stub()
        coEvery { fixture.dataSource.updateAppointmentSettings(settings) } throws
            DomainError.BusinessError(AppointmentErrorCodes.INVALID_DAY_OFF_RANGE, "msg")

        whenn()
        then()
        assertFailsWith<UpdateAppointmentSettings.Error.InvalidDayOffRange> {
            fixture.sut(settings)
        }
    }
}
