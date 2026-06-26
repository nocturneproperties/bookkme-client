package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
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
        val dataSource = mock<AppointmentSettingsDataSource>()
        val sut = UpdateAppointmentSettingsImpl(dataSource)
    }

    @Test
    fun `returns updated settings`() = runUnitTest {
        given()
        val sut = Fixture()
        val settings = AppointmentSettings.stub()
        val updated = AppointmentSettings.stub()
        everySuspend { sut.dataSource.updateAppointmentSettings(settings) } returns updated
        everySuspend { sut.dataSource.saveAppointmentSettingsInDB(updated) } returns Unit

        whenn()
        val result = sut.sut(settings)

        then()
        assertEquals(updated, result)
    }

    @Test
    fun `saves updated settings in DB`() = runUnitTest {
        given()
        val sut = Fixture()
        val settings = AppointmentSettings.stub()
        val updated = AppointmentSettings.stub()
        everySuspend { sut.dataSource.updateAppointmentSettings(settings) } returns updated
        everySuspend { sut.dataSource.saveAppointmentSettingsInDB(updated) } returns Unit

        whenn()
        sut.sut(settings)

        then()
        verifySuspend(VerifyMode.exactly(1)) { sut.dataSource.saveAppointmentSettingsInDB(updated) }
    }

    @Test
    fun `throws ActiveDayWithoutWorkHours on corresponding error code`() = runUnitTest {
        given()
        val sut = Fixture()
        val settings = AppointmentSettings.stub()
        everySuspend { sut.dataSource.updateAppointmentSettings(settings) } throws
            DomainError.BusinessError(AppointmentErrorCodes.ACTIVE_DAY_WITHOUT_WORK_HOURS, "msg")

        whenn()
        then()
        assertFailsWith<UpdateAppointmentSettings.Error.ActiveDayWithoutWorkHours> {
            sut.sut(settings)
        }
    }

    @Test
    fun `throws InvalidDayOffRange on corresponding error code`() = runUnitTest {
        given()
        val sut = Fixture()
        val settings = AppointmentSettings.stub()
        everySuspend { sut.dataSource.updateAppointmentSettings(settings) } throws
            DomainError.BusinessError(AppointmentErrorCodes.INVALID_DAY_OFF_RANGE, "msg")

        whenn()
        then()
        assertFailsWith<UpdateAppointmentSettings.Error.InvalidDayOffRange> {
            sut.sut(settings)
        }
    }
}
