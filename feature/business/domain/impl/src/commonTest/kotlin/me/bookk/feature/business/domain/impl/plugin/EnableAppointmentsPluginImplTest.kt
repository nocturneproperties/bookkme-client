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
import me.bookk.feature.business.domain.datasource.PluginDataSource
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
        val sut = EnableAppointmentsPluginImpl(pluginDataSource)
    }

    @Test
    fun `enables the plugin for the requested business id`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.pluginDataSource.enableAppointmentsPlugin(businessId) } returns Unit
        everySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) } returns Unit

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend { fixture.pluginDataSource.enableAppointmentsPlugin(businessId) }
    }

    @Test
    fun `caches the plugin as enabled after a successful call`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.pluginDataSource.enableAppointmentsPlugin(businessId) } returns Unit
        everySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) } returns Unit

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) }
    }

    @Test
    fun `throws AlreadyEnabled on PLUGIN_ALREADY_ENABLED error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.pluginDataSource.enableAppointmentsPlugin(businessId) } throws
            DomainError.BusinessError(AppointmentsErrorCodes.PLUGIN_ALREADY_ENABLED, "msg")
        everySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) } returns Unit

        whenn()
        then()
        assertFailsWith<EnableAppointmentsPlugin.Error.AlreadyEnabled> {
            fixture.sut(businessId)
        }
    }

    @Test
    fun `caches the plugin as enabled when it was already enabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.pluginDataSource.enableAppointmentsPlugin(businessId) } throws
            DomainError.BusinessError(AppointmentsErrorCodes.PLUGIN_ALREADY_ENABLED, "msg")
        everySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) } returns Unit

        whenn()
        runCatching { fixture.sut(businessId) }

        then()
        verifySuspend { fixture.pluginDataSource.saveAppointmentPluginAvailability(businessId, true) }
    }
}
