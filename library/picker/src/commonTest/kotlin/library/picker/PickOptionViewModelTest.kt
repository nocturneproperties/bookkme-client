package library.picker

import dev.icerock.moko.resources.desc.image.ImageDesc
import library.picker.state.PickOptionItem
import library.picker.state.PickOptionState
import me.bookk.core.domain.entity.KeyValueData
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeAppBarState
import me.bookk.designsystem.test.FakeBooleanState
import me.bookk.designsystem.test.FakeButtonState
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.FakeListState
import me.bookk.designsystem.test.FakeNavigationState
import me.bookk.designsystem.test.FakeTextFieldState
import me.bookk.designsystem.test.FakeViewState
import me.bookk.designsystem.test.ViewModelTestDispatchers
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PickOptionViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture(choice: PickerScreenArgs.Choice) {
        val haircut = KeyValueData("1", "Haircut")
        val coloring = KeyValueData("2", "Coloring")
        val shave = KeyValueData("3", "Shave")
        val args = PickerScreenArgs(
            id = "services",
            title = "Services",
            options = listOf(haircut, coloring, shave).map { PickerScreenArgs.PickerData(it) },
            choice = choice
        )

        fun sut() = PickOptionViewModel(
            pickArgs = args,
            factory = FakePickOptionStateFactory(),
            vmArgs = VmArgs(FakeErrorMapper())
        )
    }

    private fun PickOptionViewModel.check(data: KeyValueData) {
        uiState.filteredOptions.items.single { it.identity == data }.checkBox.onCheckedChange?.invoke(true)
    }

    @Test
    fun `shows select button for multiple choice`() = runUnitTest {
        given()
        val fixture = Fixture(PickerScreenArgs.Choice.MULTIPLE)

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.selectButton.isVisible)
    }

    @Test
    fun `finishes with every checked option on select in multiple choice`() = runUnitTest {
        given()
        val fixture = Fixture(PickerScreenArgs.Choice.MULTIPLE)
        val sut = fixture.sut()
        sut.check(fixture.haircut)
        sut.check(fixture.shave)

        whenn()
        sut.uiState.selectButton.onClick?.invoke()

        then()
        assertEquals(
            listOf<PickerNavigationDestination>(
                PickerNavigationDestination.FinishWithResult("services", listOf(fixture.haircut, fixture.shave))
            ),
            sut.uiState.navigation.navigationDestination
        )
    }

    @Test
    fun `finishes with the tapped option in single choice`() = runUnitTest {
        given()
        val fixture = Fixture(PickerScreenArgs.Choice.SINGLE)
        val sut = fixture.sut()

        whenn()
        sut.check(fixture.coloring)

        then()
        assertEquals(
            listOf<PickerNavigationDestination>(
                PickerNavigationDestination.FinishWithResult("services", listOf(fixture.coloring))
            ),
            sut.uiState.navigation.navigationDestination
        )
    }
}

private class FakePickOptionStateFactory : PickOptionStateFactory {
    override fun createPickOptionState(): PickOptionState = FakePickOptionState()
    override fun createPickOptionItem(): PickOptionItem = FakePickOptionItem()
}

private class FakePickOptionState : PickOptionState {
    override val appBar = FakeAppBarState()
    override val queryField = FakeTextFieldState()
    override val filteredOptions = FakeListState<PickOptionItem>()
    override val selectButton = FakeButtonState()
    override val navigation = FakeNavigationState<PickerNavigationDestination>()
}

private class FakePickOptionItem : FakeViewState(), PickOptionItem {
    override var identity: KeyValueData = KeyValueData("", "")
    override var icon: ImageDesc? = null
    override val checkBox = FakeBooleanState()
}
