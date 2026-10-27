package me.bookk.feature.business.presentation.screen.analytics

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.feature.business.presentation.FakeBusinessStateFactory
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BusinessAnalyticsViewModelTest {

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
        fun sut() = BusinessAnalyticsViewModel(
            stateFactory = FakeBusinessStateFactory(),
            vmArgs = VmArgs(FakeErrorMapper())
        )
    }

    @Test
    fun `shows analytics title in app bar`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(BusinessRes.strings.business_dashboard_analytics.desc(), sut.uiState.appBar.title)
    }

    @Test
    fun `shows coming soon title and message`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(BusinessRes.strings.business_analytics_coming_soon_title.desc(), sut.uiState.title)
        assertEquals(BusinessRes.strings.business_analytics_coming_soon_message.desc(), sut.uiState.message)
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf<BusinessAnalyticsDestinations>(BusinessAnalyticsDestinations.Back),
            sut.uiState.navigation.navigationDestination
        )
    }
}
