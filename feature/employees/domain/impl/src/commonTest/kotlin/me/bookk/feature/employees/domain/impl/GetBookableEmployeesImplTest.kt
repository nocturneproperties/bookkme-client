package me.bookk.feature.employees.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import me.bookk.feature.employees.domain.datasource.BookableEmployeeDataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class GetBookableEmployeesImplTest {

    private val businessId = Uuid.random()
    private val employees = listOf(bookable("Jane"), bookable("John"))

    private class Fixture {
        val dataSource = mock<BookableEmployeeDataSource>()
        val sut = GetBookableEmployeesImpl(dataSource)
    }

    private class FetchFailed : Exception()

    private fun bookable(name: String) = BookableEmployee(
        id = Uuid.random(),
        businessId = businessId,
        name = name,
        lastName = "Doe",
        services = listOf(stubService(businessId)),
        schedule = WorkingSchedule(),
    )

    @Test
    fun `emits the bookable employees saved for the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.dataSource.observeBookableEmployeesInDb(businessId) } returns
            flowOf(employees)

        whenn()
        val result = fixture.sut.flow(businessId).first()

        then()
        assertEquals(employees, result)
    }

    @Test
    fun `saves the fetched employees in place of the business's saved ones on refresh`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            everySuspend { fixture.dataSource.getBookableEmployees(businessId) } returns employees
            everySuspend {
                fixture.dataSource.replaceBookableEmployeesInDb(businessId, employees)
            } returns Unit

            whenn()
            fixture.sut.refresh(businessId)

            then()
            verifySuspend {
                fixture.dataSource.replaceBookableEmployeesInDb(businessId, employees)
            }
        }

    @Test
    fun `returns the fetched employees on refresh`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getBookableEmployees(businessId) } returns employees
        everySuspend { fixture.dataSource.replaceBookableEmployeesInDb(any(), any()) } returns Unit

        whenn()
        val result = fixture.sut.refresh(businessId)

        then()
        assertEquals(employees, result)
    }

    @Test
    fun `keeps the saved employees when the fetch fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getBookableEmployees(businessId) } throws FetchFailed()

        whenn()
        assertFailsWith<FetchFailed> { fixture.sut.refresh(businessId) }

        then()
        verifySuspend(VerifyMode.not) {
            fixture.dataSource.replaceBookableEmployeesInDb(any(), any())
        }
    }
}
