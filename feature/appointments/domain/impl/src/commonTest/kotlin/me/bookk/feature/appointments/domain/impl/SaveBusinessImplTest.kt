package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import me.bookk.core.domain.entity.Error.BusinessError
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.GetSavedBusinesses
import me.bookk.feature.appointments.domain.api.SaveBusiness.Error
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class SaveBusinessImplTest {

    private class Fixture {
        val dataSource = mock<ClientBookingDataSource>()
        val getSavedBusinesses = mock<GetSavedBusinesses> {
            everySuspend { refresh() } returns emptyList()
        }
        val sut = SaveBusinessImpl(dataSource, getSavedBusinesses)
    }

    @Test
    fun `saves the business and refreshes the saved list`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.saveBusiness(businessId) } returns Unit

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend(VerifyMode.order) {
            fixture.dataSource.saveBusiness(businessId)
            fixture.getSavedBusinesses.refresh()
        }
    }

    @Test
    fun `throws appointments not enabled for a business that takes no appointments`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val businessId = Uuid.random()
            everySuspend { fixture.dataSource.saveBusiness(businessId) } throws
                BusinessError(AppointmentErrorCodes.APPOINTMENTS_NOT_ENABLED, "")

            whenn()
            val call = suspend { fixture.sut(businessId) }

            then()
            assertFailsWith<Error.AppointmentsNotEnabled> { call() }
            verifySuspend(VerifyMode.exactly(0)) { fixture.getSavedBusinesses.refresh() }
        }

    @Test
    fun `throws limit reached when the user saved the maximum number of businesses`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val businessId = Uuid.random()
            everySuspend { fixture.dataSource.saveBusiness(businessId) } throws
                BusinessError(AppointmentErrorCodes.SAVED_BUSINESSES_LIMIT_REACHED, "")

            whenn()
            val call = suspend { fixture.sut(businessId) }

            then()
            assertFailsWith<Error.SavedBusinessesLimitReached> { call() }
        }

    @Test
    fun `passes other failures through`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.saveBusiness(businessId) } throws IllegalStateException()

        whenn()
        val call = suspend { fixture.sut(businessId) }

        then()
        assertFailsWith<IllegalStateException> { call() }
    }
}
