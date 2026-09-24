package me.bookk.feature.business.presentation.screen.create

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.feature.business.domain.api.business.CreateBusiness
import me.bookk.feature.business.presentation.FakeBusinessStateFactory
import me.bookk.feature.business.presentation.stubBusiness
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CreateBusinessViewModelTest {

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
        val createBusiness = mock<CreateBusiness>()
        val errorMapper = FakeErrorMapper()
        val stateFactory = FakeBusinessStateFactory()

        fun sut() = CreateBusinessViewModel(
            createBusiness = createBusiness,
            stateFactory = stateFactory,
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `creates state with 512 character name limit`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        assertEquals(512, fixture.stateFactory.createBusinessInitData?.maxNameLength)
    }

    @Test
    fun `disables create button for a one character name`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onBusinessNameChanged("A")

        then()
        assertEquals("A", sut.uiState.name.text)
        assertFalse(sut.uiState.createBtn.isEnabled)
    }

    @Test
    fun `enables create button for a two character name`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onBusinessNameChanged("AB")

        then()
        assertTrue(sut.uiState.createBtn.isEnabled)
    }

    @Test
    fun `creates business with typed name and navigates to main`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createBusiness(any()) } returns stubBusiness()
        val sut = fixture.sut()
        sut.onBusinessNameChanged("My Salon")

        whenn()
        sut.onCreateClick()

        then()
        verifySuspend { fixture.createBusiness("My Salon") }
        assertEquals(listOf<CreateBusinessNavigationDestination>(CreateBusinessNavigationDestination.Main), sut.uiState.navigation.navigationDestination)
        assertFalse(sut.uiState.createBtn.isLoading)
    }

    @Test
    fun `shows mapped error and stays on screen when creation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createBusiness(any()) } throws TestException()
        val sut = fixture.sut()
        sut.onBusinessNameChanged("My Salon")

        whenn()
        sut.onCreateClick()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        sut.uiState.notifications.assertSingle<PresentationNotification.GlobalMessage>()
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
        assertFalse(sut.uiState.createBtn.isLoading)
    }
}
