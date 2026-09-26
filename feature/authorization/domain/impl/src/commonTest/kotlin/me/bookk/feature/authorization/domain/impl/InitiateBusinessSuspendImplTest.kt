package me.bookk.feature.authorization.domain.impl

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
import me.bookk.feature.business.domain.api.business.RefreshBusinessInfo
import me.bookk.feature.business.domain.api.business.SwitchDashboardBusiness
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class InitiateBusinessSuspendImplTest {

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
        val switchDashboardBusiness = mock<SwitchDashboardBusiness> {
            everySuspend { invoke(null) } returns Unit
        }
        val refreshBusinessInfo = mock<RefreshBusinessInfo> {
            everySuspend { invoke(false) } returns Unit
        }
        val sut = InitiateBusinessSuspendImpl(switchDashboardBusiness, refreshBusinessInfo)
    }

    @Test
    fun `clears the dashboard business before refreshing every business`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.order) {
            fixture.switchDashboardBusiness(null)
            fixture.refreshBusinessInfo(false)
        }
    }

    @Test
    fun `keeps the dashboard business cleared when the refresh fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.refreshBusinessInfo(false) } throws DomainError.NoConnectionError(Exception())

        whenn()
        then()
        assertFailsWith<DomainError.NoConnectionError> {
            fixture.sut()
        }
        verifySuspend(VerifyMode.exactly(1)) { fixture.switchDashboardBusiness(null) }
    }
}
