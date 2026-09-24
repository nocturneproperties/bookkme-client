package me.bookk.feature.services.presentation.group.add

import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.services.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.services.domain.api.group.CreateServiceGroup
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.presentation.FakeServicesStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class AddGroupViewModelTest {

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
        val createGroup = mock<CreateServiceGroup>()
        val observeCurrentBusinessId = mock<ObserveCurrentBusinessId> {
            every { invoke() } returns MutableStateFlow<Uuid?>(businessId)
        }
        val errorMapper = FakeErrorMapper()
        val created = Capture.slot<ServiceGroup>()

        fun sut() = AddGroupViewModel(
            createGroup = createGroup,
            observeCurrentBusinessId = observeCurrentBusinessId,
            stateFactory = FakeServicesStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    private fun AddGroupViewModel.typeName(name: String) {
        (uiState.name as FakeTextFieldState).type(name)
    }

    @Test
    fun `disables create initially`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.create.isEnabled)
    }

    @Test
    fun `enables create for names longer than one character`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.typeName("Ha")

        then()
        assertTrue(sut.uiState.create.isEnabled)
    }

    @Test
    fun `clears name error on typing`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        sut.uiState.name.validationState = ValidationState.ERROR

        whenn()
        sut.typeName("Hair")

        then()
        assertEquals(ValidationState.DEFAULT, sut.uiState.name.validationState)
        assertEquals(null, sut.uiState.name.supportingTextRes)
    }

    @Test
    fun `creates group for current business and dismisses`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createGroup(capture(fixture.created)) } calls { (group: ServiceGroup) -> group }
        val sut = fixture.sut()
        sut.typeName("Hair")

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        assertEquals("Hair", fixture.created.get().name)
        assertEquals(fixture.businessId, fixture.created.get().businessId)
        assertEquals(listOf<AddGroupNavigation>(AddGroupNavigation.Dismiss), sut.uiState.navigation.navigationDestination)
    }

    @Test
    fun `shows field error when name already exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createGroup(any()) } throws CreateServiceGroup.Error.NameExists(TestException())
        val sut = fixture.sut()
        sut.typeName("Hair")

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        assertEquals(ValidationState.ERROR, sut.uiState.name.validationState)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }

    @Test
    fun `shows field error when name is invalid`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createGroup(any()) } throws CreateServiceGroup.Error.InvalidName(TestException())
        val sut = fixture.sut()
        sut.typeName("Hair")

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        assertEquals(ValidationState.ERROR, sut.uiState.name.validationState)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `shows mapped error when creation fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createGroup(any()) } throws TestException()
        val sut = fixture.sut()
        sut.typeName("Hair")

        whenn()
        sut.uiState.create.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertFalse(sut.uiState.create.isLoading)
    }
}
