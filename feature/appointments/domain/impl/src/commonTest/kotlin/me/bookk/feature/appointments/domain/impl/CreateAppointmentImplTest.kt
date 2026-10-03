package me.bookk.feature.appointments.domain.impl

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
import kotlinx.datetime.LocalDateTime
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.CreateAppointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentDraft
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.UserProfile
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class CreateAppointmentImplTest {

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
        val appointmentDataSource = mock<AppointmentDataSource>()
        val userProfileCRUD = mock<UserProfileCRUD>()
        val sut = CreateAppointmentImpl(appointmentDataSource, userProfileCRUD)
        val userId = Uuid.random()
        val profile = UserProfile(id = userId, firstName = "John", lastName = "Doe", email = "john@example.com")

        init {
            everySuspend { userProfileCRUD.get() } returns profile
            everySuspend { appointmentDataSource.saveAppointmentInDB(any()) } returns Unit
        }
    }

    private fun stubDraft(businessId: Uuid = Uuid.random()) = AppointmentDraft(
        businessId = businessId,
        client = stubClientSnapshot(),
        services = listOf(stubServiceSnapshot()),
        date = LocalDateTime(2024, 1, 15, 10, 0),
        note = "test note"
    )

    @Test
    fun `returns created appointment from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val draft = stubDraft()
        val created = stubAppointment()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } returns created

        whenn()
        val result = fixture.sut(draft)

        then()
        assertEquals(created, result)
    }

    @Test
    fun `uses employee derived from profile in appointment`() = runUnitTest {
        given()
        val fixture = Fixture()
        val draft = stubDraft()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } returns stubAppointment()

        whenn()
        fixture.sut(draft)

        then()
        verifySuspend {
            fixture.appointmentDataSource.createAppointment(
                matches({ "match" }) {
                    it.employee.id == fixture.userId &&
                        it.employee.userId == fixture.userId &&
                        it.employee.fullName == "${fixture.profile.firstName} ${fixture.profile.lastName}"
                }
            )
        }
    }

    @Test
    fun `uses userId from profile in appointment`() = runUnitTest {
        given()
        val fixture = Fixture()
        val draft = stubDraft()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } returns stubAppointment()

        whenn()
        fixture.sut(draft)

        then()
        verifySuspend {
            fixture.appointmentDataSource.createAppointment(
                matches({ "match" }) { it.userId == fixture.userId && it.status == AppointmentStatus.SCHEDULED }
            )
        }
    }

    @Test
    fun `saves created appointment in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val created = stubAppointment()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } returns created

        whenn()
        fixture.sut(stubDraft())

        then()
        verifySuspend { fixture.appointmentDataSource.saveAppointmentInDB(created) }
    }

    @Test
    fun `throws AppointmentOverlap on APPOINTMENT_EXISTS error`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws
            DomainError.BusinessError(AppointmentErrorCodes.APPOINTMENT_EXISTS, "msg")

        whenn()
        then()
        assertFailsWith<CreateAppointment.Error.AppointmentOverlap> {
            fixture.sut(stubDraft())
        }
    }

    @Test
    fun `throws TimeIsNotAllowed on TIME_NOT_ALLOWED error`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws
            DomainError.BusinessError(AppointmentErrorCodes.TIME_NOT_ALLOWED, "msg")

        whenn()
        then()
        assertFailsWith<CreateAppointment.Error.TimeIsNotAllowed> {
            fixture.sut(stubDraft())
        }
    }

    @Test
    fun `throws DateIsNotAllowed on DATE_NOT_ALLOWED error`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws
            DomainError.BusinessError(AppointmentErrorCodes.DATE_NOT_ALLOWED, "msg")

        whenn()
        then()
        assertFailsWith<CreateAppointment.Error.DateIsNotAllowed> {
            fixture.sut(stubDraft())
        }
    }
}
