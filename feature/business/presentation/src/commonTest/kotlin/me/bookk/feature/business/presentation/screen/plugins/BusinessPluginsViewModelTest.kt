package me.bookk.feature.business.presentation.screen.plugins

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertEmpty
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.feature.business.domain.api.plugin.EnableAppointmentsPlugin
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.presentation.FakeBusinessStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class BusinessPluginsViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture {
        val businessId = Uuid.random()
        val pluginFlow = MutableStateFlow<Boolean?>(null)
        val isAppointmentsPluginEnabled = mock<IsAppointmentsPluginEnabled> {
            every { flow(any()) } returns pluginFlow
            everySuspend { refresh(any()) } returns false
        }
        val enableAppointmentsPlugin = mock<EnableAppointmentsPlugin>()
        val errorMapper = FakeErrorMapper()

        fun sut() = BusinessPluginsViewModel(
            businessId = businessId,
            isAppointmentsPluginEnabled = isAppointmentsPluginEnabled,
            enableAppointmentsPlugin = enableAppointmentsPlugin,
            stateFactory = FakeBusinessStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `refreshes plugin availability on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.isAppointmentsPluginEnabled.refresh(fixture.businessId) }
    }

    @Test
    fun `renders cached plugin availability`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.pluginFlow.value = true

        then()
        assertTrue(sut.uiState.appointmentPlugin.isEnabled)
    }

    @Test
    fun `marks plugin enabled after enable succeeds`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.enableAppointmentsPlugin(any()) } returns Unit
        val sut = fixture.sut()

        whenn()
        sut.uiState.appointmentPlugin.enable.onClick?.invoke()

        then()
        assertTrue(sut.uiState.appointmentPlugin.isEnabled)
        assertFalse(sut.uiState.appointmentPlugin.enable.isLoading)
    }

    @Test
    fun `treats already enabled error as enabled without notification`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.enableAppointmentsPlugin(any()) } throws EnableAppointmentsPlugin.Error.AlreadyEnabled()
        val sut = fixture.sut()

        whenn()
        sut.uiState.appointmentPlugin.enable.onClick?.invoke()

        then()
        assertTrue(sut.uiState.appointmentPlugin.isEnabled)
        sut.uiState.notifications.assertEmpty()
    }

    @Test
    fun `shows notification when enable fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.enableAppointmentsPlugin(any()) } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.uiState.appointmentPlugin.enable.onClick?.invoke()

        then()
        assertFalse(sut.uiState.appointmentPlugin.isEnabled)
        fixture.errorMapper.assertMappedSingle(TestException::class)
        sut.uiState.notifications.assertSingle<PresentationNotification.GlobalMessage>()
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf<BusinessPluginsDestinations>(BusinessPluginsDestinations.Back), sut.uiState.navigation.navigationDestination)
    }
}
