package me.bookk.feature.services.presentation.service.edit

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.services.resources.ServicesRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.feature.services.presentation.FakeServicesStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

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

    private class Fixture {
        val serviceId = Uuid.random()
        val errorMapper = FakeErrorMapper()

        fun sut() = EditServiceViewModel(
            serviceId = serviceId,
            stateFactory = FakeServicesStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )
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
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf<EditServiceDestination>(EditServiceDestination.Back), sut.uiState.navigation.navigationDestination)
    }
}
