package me.bookk.feature.services.presentation.service.edit

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import library.money.api.Money
import me.bookk.android.feature.services.resources.ServicesRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
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
import me.bookk.designsystem.test.assertSingle
import me.bookk.designsystem.test.tap
import me.bookk.feature.services.domain.api.GetServicesPermissions
import me.bookk.feature.services.domain.api.entity.ServicesPermissions
import me.bookk.feature.services.domain.api.group.GetServiceGroups
import me.bookk.feature.services.domain.api.service.DeleteService
import me.bookk.feature.services.domain.api.service.EditService
import me.bookk.feature.services.domain.api.service.GetService
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.presentation.FakeServicesStateFactory
import me.bookk.feature.services.presentation.service.ServiceGroupOption
import me.bookk.feature.services.presentation.stubGroup
import me.bookk.feature.services.presentation.stubService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class EditServiceViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture(permissions: ServicesPermissions = ServicesPermissions(canEdit = true, canDelete = true)) {
        val group = stubGroup("Hair")
        val otherGroup = stubGroup("Nails", group.businessId)
        val service = stubService(group = group)
        val getService = mock<GetService> {
            everySuspend { invoke(any()) } returns service
        }
        val editService = mock<EditService>()
        val deleteService = mock<DeleteService>()
        val getServiceGroups = mock<GetServiceGroups> {
            everySuspend { refresh(any()) } returns listOf(group, otherGroup)
        }
        val getServicesPermissions = mock<GetServicesPermissions> {
            everySuspend { invoke(any()) } returns permissions
        }
        val errorMapper = FakeErrorMapper()
        val edited = Capture.slot<Service>()

        fun sut() = EditServiceViewModel(
            serviceId = service.id,
            getService = getService,
            editService = editService,
            deleteService = deleteService,
            getServiceGroups = getServiceGroups,
            getServicesPermissions = getServicesPermissions,
            stateFactory = FakeServicesStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun EditServiceViewModel.pickGroup(group: ServiceGroupOption) {
        (uiState.group as FakePickerFieldState<ServiceGroupOption>).pick(group)
    }

    @Test
    fun `shows edit service title`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(ServicesRes.strings.services_edit_title.desc(), sut.uiState.appBar.title)
    }

    @Test
    fun `fills the form with the stored service`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(ServiceGroupOption(fixture.group), sut.uiState.group.selectedItem)
        assertEquals("Hair".desc(), (sut.uiState.group.textField as FakeTextFieldState).lastUpdatedDesc)
        assertEquals("Haircut", sut.uiState.name.text)
        assertEquals("30", sut.uiState.duration.text)
        assertEquals("100.0", sut.uiState.price.text)
        assertTrue(sut.uiState.available.isChecked)
    }

    @Test
    fun `enables save for the untouched stored service`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.submit.isEnabled)
    }

    @Test
    fun `loads group options of the service business`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        verifySuspend { fixture.getServiceGroups.refresh(fixture.service.businessId) }
        assertEquals(listOf(fixture.group, fixture.otherGroup), sut.uiState.group.options.map { it.domain })
    }

    @Test
    fun `saves edited fields through edit service`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editService(capture(fixture.edited)) } returns fixture.service
        val sut = fixture.sut()
        sut.pickGroup(ServiceGroupOption(fixture.otherGroup))
        (sut.uiState.name as FakeTextFieldState).type("Color")
        (sut.uiState.duration as FakeTextFieldState).type("45")
        (sut.uiState.price as FakeTextFieldState).type("12,5")
        sut.uiState.available.onCheckedChange?.invoke(false)

        whenn()
        sut.uiState.submit.onClick?.invoke()

        then()
        assertEquals(
            fixture.service.copy(
                group = fixture.otherGroup,
                name = "Color",
                duration = 45.minutes,
                price = Money(1250L, Money.SupportedCurrency.UAH),
                isAvailable = false
            ),
            fixture.edited.get()
        )
    }

    @Test
    fun `navigates back after saving`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editService(any()) } returns fixture.service
        val sut = fixture.sut()

        whenn()
        sut.uiState.submit.onClick?.invoke()

        then()
        assertEquals(listOf<EditServiceDestination>(EditServiceDestination.Back), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `shows mapped error and stays when saving fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.editService(any()) } throws TestException()
        val sut = fixture.sut()

        whenn()
        sut.uiState.submit.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertEquals(emptyList(), sut.uiState.navigation.navigationDestination)
        assertTrue(sut.uiState.submit.isEnabled)
    }

    @Test
    fun `disables save when name is blank`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        (sut.uiState.name as FakeTextFieldState).type(" ")

        then()
        assertFalse(sut.uiState.submit.isEnabled)
    }

    @Test
    fun `disables save when duration is empty`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        (sut.uiState.duration as FakeTextFieldState).type("")

        then()
        assertFalse(sut.uiState.submit.isEnabled)
    }

    @Test
    fun `disables save when price is not a number`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        (sut.uiState.price as FakeTextFieldState).type("1.2.3")

        then()
        assertFalse(sut.uiState.submit.isEnabled)
    }

    @Test
    fun `locks the form and hides save for a user who cannot edit services`() = runUnitTest {
        given()
        val fixture = Fixture(ServicesPermissions(canEdit = false, canDelete = false))

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.submit.isVisible)
        assertFalse(sut.uiState.group.textField.enabled)
        assertFalse(sut.uiState.name.enabled)
        assertFalse(sut.uiState.duration.enabled)
        assertFalse(sut.uiState.price.enabled)
        assertFalse(sut.uiState.available.isEnabled)
    }

    @Test
    fun `shows delete for a user who can delete services`() = runUnitTest {
        given()
        val fixture = Fixture(ServicesPermissions(canEdit = false, canDelete = true))

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.deleteButton.isVisible)
    }

    @Test
    fun `hides delete for a user who cannot delete services`() = runUnitTest {
        given()
        val fixture = Fixture(ServicesPermissions(canEdit = true, canDelete = false))

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.deleteButton.isVisible)
    }

    @Test
    fun `deletes service after confirmation and navigates back`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deleteService(any()) } returns Unit
        val sut = fixture.sut()
        sut.uiState.deleteButton.onClick?.invoke()

        whenn()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>().tap(ActionType.NEGATIVE)

        then()
        verifySuspend { fixture.deleteService(fixture.service) }
        assertEquals(listOf<EditServiceDestination>(EditServiceDestination.Back), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `shows mapped error when the service cannot be loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getService(any()) } throws GetService.Error.NotFound()

        whenn()
        val sut = fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(GetService.Error.NotFound::class)
        assertFalse(sut.uiState.submit.isEnabled)
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf<EditServiceDestination>(EditServiceDestination.Back), sut.uiState.navigation.navigationDestination)
    }
}
