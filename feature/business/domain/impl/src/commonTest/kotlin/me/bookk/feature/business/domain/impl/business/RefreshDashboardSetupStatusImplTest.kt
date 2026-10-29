package me.bookk.feature.business.domain.impl.business

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
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
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.service.GetServices
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshDashboardSetupStatusImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class RefreshFailure : Exception()

    private class Fixture {
        val isAppointmentsPluginEnabled = mock<IsAppointmentsPluginEnabled> {
            everySuspend { refresh(any()) } returns true
        }
        val getServiceGroups = mock<GetServiceGroups> {
            everySuspend { refresh(any()) } returns emptyList()
        }
        val getServices = mock<GetServices> {
            everySuspend { refresh(any()) } returns emptyList()
        }
        val getClientsList = mock<GetClientsList> {
            everySuspend { refresh(any()) } returns emptyList()
        }
        val sut = RefreshDashboardSetupStatusImpl(isAppointmentsPluginEnabled, getServiceGroups, getServices, getClientsList)
    }

    @Test
    fun `refreshes every setup step of the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend {
            fixture.isAppointmentsPluginEnabled.refresh(businessId)
            fixture.getServiceGroups.refresh(businessId)
            fixture.getServices.refresh(businessId)
            fixture.getClientsList.refresh(businessId)
        }
    }

    @Test
    fun `still refreshes the remaining steps when plugin refresh fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.isAppointmentsPluginEnabled.refresh(any()) } throws RefreshFailure()

        whenn()
        runCatching { fixture.sut(businessId) }

        then()
        verifySuspend {
            fixture.getServiceGroups.refresh(businessId)
            fixture.getServices.refresh(businessId)
            fixture.getClientsList.refresh(businessId)
        }
    }

    @Test
    fun `rethrows the refresh failure after refreshing the rest`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getServiceGroups.refresh(any()) } throws RefreshFailure()

        whenn()
        val result = runCatching { fixture.sut(Uuid.random()) }

        then()
        assertFailsWith<RefreshFailure> { result.getOrThrow() }
    }
}
