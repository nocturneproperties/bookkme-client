package me.bookk.feature.business.domain.impl.plugin

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
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
import me.bookk.feature.business.domain.api.plugin.EnableAppointmentsPlugin
import me.bookk.feature.business.domain.datasource.AppointmentsErrorCodes
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.datasource.PluginDataSource
import me.bookk.feature.business.domain.impl.stubBusiness
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class EnableAppointmentsPluginImplTest {

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
        val pluginDataSource = mock<PluginDataSource>()
        val businessDataSource = mock<BusinessDataSource>()
        val sut = EnableAppointmentsPluginImpl(pluginDataSource, businessDataSource)
    }

    @Test
    fun `calls enableAppointmentsPlugin with correct business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val business = stubBusiness(id = businessId)
        everySuspend { fixture.businessDataSource.getBusinessById(businessId) } returns business
        everySuspend { fixture.pluginDataSource.enableAppointmentsPlugin(business) } returns Unit

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend { fixture.pluginDataSource.enableAppointmentsPlugin(business) }
    }

    @Test
    fun `throws AlreadyEnabled on PLUGIN_ALREADY_ENABLED error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val business = stubBusiness(id = businessId)
        everySuspend { fixture.businessDataSource.getBusinessById(businessId) } returns business
        everySuspend { fixture.pluginDataSource.enableAppointmentsPlugin(business) } throws
            DomainError.BusinessError(AppointmentsErrorCodes.PLUGIN_ALREADY_ENABLED, "msg")

        whenn()
        then()
        assertFailsWith<EnableAppointmentsPlugin.Error.AlreadyEnabled> {
            fixture.sut(businessId)
        }
    }

    @Test
    fun `throws when business not found`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.businessDataSource.getBusinessById(businessId) } returns null

        whenn()
        then()
        assertFailsWith<IllegalStateException> {
            fixture.sut(businessId)
        }
    }
}
