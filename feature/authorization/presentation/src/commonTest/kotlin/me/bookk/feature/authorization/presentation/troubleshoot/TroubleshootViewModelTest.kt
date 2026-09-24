package me.bookk.feature.authorization.presentation.troubleshoot

import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.feature.authorization.presentation.FakeAuthStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class TroubleshootViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    @Test
    fun `offers lost and missing passkey reasons with distinct ids`() = runUnitTest {
        given()
        val stateFactory = FakeAuthStateFactory()

        whenn()
        val sut = TroubleshootViewModel(stateFactory, VmArgs(FakeErrorMapper()))

        then()
        val reasons = sut.uiState.troubleshootCardStaticData.reasons
        assertEquals(listOf(0, 1), reasons.map { it.id })
    }
}
