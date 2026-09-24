package me.bookk.feature.settings.presentation.contactus

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.feature.settings.domain.api.SendContactForm
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ContactUsViewModelTest {

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
        val sendContactForm = mock<SendContactForm>()
        val errorMapper = FakeErrorMapper()

        fun sut() = ContactUsViewModel(
            sendContactForm = sendContactForm,
            settingsStateFactory = FakeSettingsStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
    }

    @Test
    fun `enables submit for non blank text`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onContactTextChanged("Hello")

        then()
        assertEquals("Hello", sut.uiState.contactField.text)
        assertTrue(sut.uiState.submitButton.isEnabled)
    }

    @Test
    fun `disables submit for blank text`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.onContactTextChanged("   ")

        then()
        assertFalse(sut.uiState.submitButton.isEnabled)
    }

    @Test
    fun `sends text with logs choice and goes back`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.sendContactForm(any(), any()) } returns Unit
        val sut = fixture.sut()
        sut.onContactTextChanged("Hello")
        sut.onIncludeLogsStateChanged(true)

        whenn()
        sut.onSubmitClick()

        then()
        verifySuspend { fixture.sendContactForm("Hello", true) }
        assertEquals(listOf<ContactUsNavigationDestination>(ContactUsNavigationDestination.Back), sut.uiState.navigation.navigationDestination)
        assertFalse(sut.uiState.submitButton.isLoading)
    }

    @Test
    fun `shows mapped error and stays when sending fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.sendContactForm(any(), any()) } throws TestException()
        val sut = fixture.sut()
        sut.onContactTextChanged("Hello")

        whenn()
        sut.onSubmitClick()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
        assertTrue(sut.uiState.navigation.navigationDestination.isEmpty())
    }
}
