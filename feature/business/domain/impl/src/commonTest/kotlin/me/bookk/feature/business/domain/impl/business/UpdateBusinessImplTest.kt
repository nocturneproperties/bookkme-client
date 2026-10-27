package me.bookk.feature.business.domain.impl.business

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.DayOfWeek
import library.money.api.Currency
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.business.UpdateBusiness
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.datasource.BusinessErrorCodes
import me.bookk.feature.business.domain.impl.stubBusiness
import me.bookk.feature.business.domain.impl.stubDayOff
import me.bookk.feature.business.domain.impl.stubDaySchedule
import me.bookk.feature.business.domain.impl.stubWorkingSchedule
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateBusinessImplTest {

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
        val dataSource = mock<BusinessDataSource>()
        val sut = UpdateBusinessImpl(dataSource)
    }

    private fun stubUpdate(
        id: Uuid,
        schedule: WorkingSchedule = stubWorkingSchedule()
    ) = Business.Update(
        id = id,
        name = "New Name",
        description = "New Desc",
        address = "New Address",
        location = null,
        currency = Currency("USD"),
        socials = emptyMap(),
        schedule = schedule
    )

    @Test
    fun `returns updated business with new fields`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        val update = stubUpdate(id)
        everySuspend { fixture.dataSource.getBusinessById(id) } returns stubBusiness(id)
        everySuspend { fixture.dataSource.updateBusiness(any()) } returns Unit
        everySuspend { fixture.dataSource.saveBusinessInDB(any()) } returns Unit

        whenn()
        val result = fixture.sut(update)

        then()
        assertEquals(update.name, result.name)
        assertEquals(update.description, result.description)
        assertEquals(update.address, result.address)
    }

    @Test
    fun `returns business carrying the updated working schedule`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        val newSchedule = stubWorkingSchedule(
            days = mapOf(DayOfWeek.MONDAY to stubDaySchedule(isActive = false)),
            dayOffs = listOf(stubDayOff())
        )
        val update = stubUpdate(id, schedule = newSchedule)
        everySuspend { fixture.dataSource.getBusinessById(id) } returns stubBusiness(id)
        everySuspend { fixture.dataSource.updateBusiness(any()) } returns Unit
        everySuspend { fixture.dataSource.saveBusinessInDB(any()) } returns Unit

        whenn()
        val result = fixture.sut(update)

        then()
        assertEquals(newSchedule, result.schedule)
    }

    @Test
    fun `sends the updated schedule to the data source`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        val newSchedule = stubWorkingSchedule(dayOffs = listOf(stubDayOff()))
        val update = stubUpdate(id, schedule = newSchedule)
        everySuspend { fixture.dataSource.getBusinessById(id) } returns stubBusiness(id)
        everySuspend { fixture.dataSource.updateBusiness(any()) } returns Unit
        everySuspend { fixture.dataSource.saveBusinessInDB(any()) } returns Unit

        whenn()
        fixture.sut(update)

        then()
        verifySuspend {
            fixture.dataSource.updateBusiness(matches({ "business with updated schedule" }) {
                it.schedule == newSchedule
            })
        }
    }

    @Test
    fun `calls updateBusiness and saveBusinessInDB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        val update = stubUpdate(id)
        everySuspend { fixture.dataSource.getBusinessById(id) } returns stubBusiness(id)
        everySuspend { fixture.dataSource.updateBusiness(any()) } returns Unit
        everySuspend { fixture.dataSource.saveBusinessInDB(any()) } returns Unit

        whenn()
        fixture.sut(update)

        then()
        verifySuspend { fixture.dataSource.updateBusiness(any()) }
        verifySuspend { fixture.dataSource.saveBusinessInDB(any()) }
    }

    @Test
    fun `throws ActiveDayWithoutWorkHours on corresponding error code`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.getBusinessById(id) } returns stubBusiness(id)
        everySuspend { fixture.dataSource.updateBusiness(any()) } throws
            DomainError.BusinessError(BusinessErrorCodes.BUSINESS_ACTIVE_DAY_WITHOUT_WORK_HOURS, "msg")

        whenn()
        then()
        assertFailsWith<UpdateBusiness.Error.ActiveDayWithoutWorkHours> {
            fixture.sut(stubUpdate(id))
        }
    }

    @Test
    fun `throws InvalidDayOffRange on corresponding error code`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.getBusinessById(id) } returns stubBusiness(id)
        everySuspend { fixture.dataSource.updateBusiness(any()) } throws
            DomainError.BusinessError(BusinessErrorCodes.BUSINESS_INVALID_DAY_OFF_RANGE, "msg")

        whenn()
        then()
        assertFailsWith<UpdateBusiness.Error.InvalidDayOffRange> {
            fixture.sut(stubUpdate(id))
        }
    }

    @Test
    fun `does not persist the business when the update call fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.getBusinessById(id) } returns stubBusiness(id)
        everySuspend { fixture.dataSource.updateBusiness(any()) } throws
            DomainError.BusinessError(BusinessErrorCodes.BUSINESS_INVALID_DAY_OFF_RANGE, "msg")

        whenn()
        runCatching { fixture.sut(stubUpdate(id)) }

        then()
        verifySuspend(dev.mokkery.verify.VerifyMode.not) { fixture.dataSource.saveBusinessInDB(any()) }
    }

    @Test
    fun `throws when business not found`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.getBusinessById(id) } returns null

        whenn()
        val thrown = runCatching { fixture.sut(stubUpdate(id)) }.exceptionOrNull()

        then()
        assertNotNull(thrown)
    }
}
