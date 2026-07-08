package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.settings.domain.api.GetNotificationSettings
import me.bookk.feature.settings.domain.api.UpdateNotificationToken
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class LowPriorityDataFetchTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun stubBusiness(id: Uuid = Uuid.random()) = Business(
        id = id,
        name = "Test Business",
        description = "",
        address = "",
        location = null,
        currency = Currency("USD"),
        timeZone = TimeZone.UTC,
        socials = emptyMap()
    )

    private class Fixture {
        val applicationScope = CoroutineScope(UnconfinedTestDispatcher())
        val updateNotificationToken = mock<UpdateNotificationToken>()
        val getServices = mock<GetServices>()
        val getServiceGroups = mock<GetServiceGroups>()
        val getClientsList = mock<GetClientsList>()
        val getNotificationSettings = mock<GetNotificationSettings>()
        val getAppointmentEnabled = mock<IsAppointmentsPluginEnabled>()
        val getAppointmentSettings = mock<GetAppointmentSettings>()
        val observeDashboardBusinessChanges = mock<ObserveDashboardBusinessChanges>()
        val sut = LowPriorityDataFetch(
            applicationScope,
            updateNotificationToken,
            getServices,
            getServiceGroups,
            getClientsList,
            getNotificationSettings,
            getAppointmentEnabled,
            getAppointmentSettings,
            observeDashboardBusinessChanges
        )
    }

    private fun stubHappyPath(fixture: Fixture, business: Business) {
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        everySuspend { fixture.getServices(business.id) } returns emptyList()
        everySuspend { fixture.getServiceGroups(business.id) } returns emptyList()
        everySuspend { fixture.getClientsList(business.id) } returns emptyList()
        everySuspend { fixture.getAppointmentEnabled(business.id) } returns true
        everySuspend { fixture.getAppointmentSettings(business.id) } returns AppointmentSettings.stub()
        everySuspend { fixture.updateNotificationToken() } returns Unit
        everySuspend { fixture.getNotificationSettings() } returns NotificationSettings.stub()
    }

    @Test
    fun `does nothing when there is no dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(null)

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.getServices(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.getServiceGroups(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.getClientsList(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.getAppointmentEnabled(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.getAppointmentSettings(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.updateNotificationToken() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.getNotificationSettings() }
    }

    @Test
    fun `waits for the first non-null business emission`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(null, business)
        stubHappyPath(fixture, business)

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getServices(business.id) }
    }

    @Test
    fun `fetches all low priority data for the current business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        stubHappyPath(fixture, business)

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getServices(business.id) }
        verifySuspend { fixture.getServiceGroups(business.id) }
        verifySuspend { fixture.getClientsList(business.id) }
        verifySuspend { fixture.getAppointmentEnabled(business.id) }
        verifySuspend { fixture.getAppointmentSettings(business.id) }
        verifySuspend { fixture.updateNotificationToken() }
        verifySuspend { fixture.getNotificationSettings() }
    }

    @Test
    fun `still fetches appointment settings and notification data when the first group fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        stubHappyPath(fixture, business)
        everySuspend { fixture.getServices(business.id) } throws RuntimeException("network error")

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getAppointmentSettings(business.id) }
        verifySuspend { fixture.updateNotificationToken() }
        verifySuspend { fixture.getNotificationSettings() }
    }

    @Test
    fun `still fetches notification data when appointment settings fail`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        stubHappyPath(fixture, business)
        everySuspend { fixture.getAppointmentSettings(business.id) } throws RuntimeException("network error")

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.updateNotificationToken() }
        verifySuspend { fixture.getNotificationSettings() }
    }

    @Test
    fun `does not propagate when the notification group fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        stubHappyPath(fixture, business)
        everySuspend { fixture.updateNotificationToken() } throws RuntimeException("network error")

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.updateNotificationToken() }
    }
}
