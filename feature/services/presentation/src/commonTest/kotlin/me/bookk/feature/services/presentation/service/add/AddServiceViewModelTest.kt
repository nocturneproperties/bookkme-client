package me.bookk.feature.services.presentation.service.add

import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import library.money.api.Money
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakePickerFieldState
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.feature.services.domain.api.GetBusinessCurrency
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.service.CreateService
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.presentation.FakeServicesStateFactory
import me.bookk.feature.services.presentation.stubGroup
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class AddServiceViewModelTest {

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
        val group = stubGroup("Hair", businessId)
        val createService = mock<CreateService>()
        val getServiceGroups = mock<GetServiceGroups> {
            everySuspend { refresh(any()) } returns listOf(group)
        }
        val getBusinessCurrency = mock<GetBusinessCurrency> {
            everySuspend { invoke(any()) } returns Money.SupportedCurrency.UAH
        }
        val errorMapper = FakeErrorMapper()
        val created = Capture.slot<Service>()

        fun sut() = AddServiceViewModel(
            businessId = businessId,
            createService = createService,
            getServiceGroups = getServiceGroups,
            getBusinessCurrency = getBusinessCurrency,
            stateFactory = FakeServicesStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun AddServiceViewModel.fillValidForm(duration: String = "30", price: String = "12,5") {
        (uiState.group as FakePickerFieldState<AddServiceState.GroupUI>).pick(uiState.group.options.first())
        (uiState.name as FakeTextFieldState).type("Haircut")
        (uiState.duration as FakeTextFieldState).type(duration)
        (uiState.price as FakeTextFieldState).type(price)
    }

    @Test
    fun `loads group options for business`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(listOf(fixture.group), sut.uiState.group.options.map { it.domain })
    }

    @Test
    fun `disables create until the form is complete`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        (sut.uiState.name as FakeTextFieldState).type("Haircut")

        then()
        assertFalse(sut.uiState.create.isEnabled)
    }

    @Test
    fun `enables create when group name duration and price are valid`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.fillValidForm()

        then()
        assertTrue(sut.uiState.create.isEnabled)
    }

    @Test
    fun `keeps only digits in duration`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        (sut.uiState.duration as FakeTextFieldState).type("4a5")

        then()
        assertEquals("45", sut.uiState.duration.text)
    }

    @Test
    fun `normalizes comma price to dot`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        (sut.uiState.price as FakeTextFieldState).type("12,50 грн")

        then()
        assertEquals("12.50", sut.uiState.price.text)
        assertTrue(sut.uiState.price.isValid)
    }

    @Test
    fun `marks unparseable price invalid`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        (sut.uiState.price as FakeTextFieldState).type("1.2.3")

        then()
        assertFalse(sut.uiState.price.isValid)
    }

    @Test
    fun `creates service with duration in minutes shown by the field suffix`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createService(capture(fixture.created)) } calls { (service: Service) -> service }
        val sut = fixture.sut()
        sut.fillValidForm(duration = "30")

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        assertEquals(30.minutes, fixture.created.get().duration)
    }

    @Test
    fun `creates service with picked group price and visibility then goes back`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createService(capture(fixture.created)) } calls { (service: Service) -> service }
        val sut = fixture.sut()
        sut.fillValidForm(price = "12,5")
        sut.uiState.enabled.onCheckedChange?.invoke(true)

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        val service = fixture.created.get()
        assertEquals("Haircut", service.name)
        assertEquals(fixture.group, service.group)
        assertEquals(Money(12.5, Money.SupportedCurrency.UAH), service.price)
        assertTrue(service.isAvailable)
        assertEquals(listOf<AddServiceDestination>(AddServiceDestination.Back), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `does not create service without picked group`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        verifySuspend(VerifyMode.not) { fixture.createService(any()) }
    }

    @Test
    fun `shows mapped error when creation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createService(any()) } throws TestException()
        val sut = fixture.sut()
        sut.fillValidForm()

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.create.isLoading)
    }

    @Test
    fun `shows mapped error when groups cannot be loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getServiceGroups.refresh(any()) } throws TestException()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }
}
