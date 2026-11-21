package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class OptionsMultiPickerQuantityTest {

    private class Fixture {
        val haircut = MinimalPickerPresentation("haircut", "Haircut".desc())
        val beard = MinimalPickerPresentation("beard", "Beard trim".desc())
        val state = QuantityTestPickerState<MinimalPickerPresentation>()

        fun select(vararg picked: Pair<MinimalPickerPresentation, Int>) {
            state.replaceSelected(
                picked.map { (item, quantity) ->
                    state.createPickedItem(item, quantity)
                },
            )
            state.replaceCount = 0
        }

        fun selection(): List<Pair<String, Int>> {
            return state.selectedItems.map { it.item.pickerItemId to it.quantity }
        }
    }

    @Test
    fun `increaseQuantity appends an unpicked item with quantity one`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.select(fixture.haircut to 1)

        whenn()
        fixture.state.increaseQuantity(fixture.beard)

        then()
        assertEquals(listOf("haircut" to 1, "beard" to 1), fixture.selection())
    }

    @Test
    fun `increaseQuantity adds one to the picked item without replacing the selection`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.select(fixture.haircut to 2, fixture.beard to 1)
            val haircut = fixture.state.selectedItems.first()

            whenn()
            fixture.state.increaseQuantity(fixture.haircut)

            then()
            assertEquals(3, haircut.quantity)
            assertSame(haircut, fixture.state.selectedItems.first())
            assertEquals(0, fixture.state.replaceCount)
        }

    @Test
    fun `increaseQuantity matches a picked item by picker item id`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.select(fixture.haircut to 1)

        whenn()
        fixture.state.increaseQuantity(fixture.haircut.copy(displayName = "Men's haircut".desc()))

        then()
        assertEquals(listOf("haircut" to 2), fixture.selection())
    }

    @Test
    fun `decreaseQuantity removes one from the picked item without replacing the selection`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.select(fixture.haircut to 3, fixture.beard to 1)
            val haircut = fixture.state.selectedItems.first()

            whenn()
            fixture.state.decreaseQuantity(fixture.haircut)

            then()
            assertEquals(2, haircut.quantity)
            assertSame(haircut, fixture.state.selectedItems.first())
            assertEquals(0, fixture.state.replaceCount)
        }

    @Test
    fun `decreaseQuantity removes an item whose quantity reaches zero`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.select(fixture.haircut to 1, fixture.beard to 2)

        whenn()
        fixture.state.decreaseQuantity(fixture.haircut)

        then()
        assertEquals(listOf("beard" to 2), fixture.selection())
    }

    @Test
    fun `decreaseQuantity leaves the selection unchanged for an unpicked item`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.select(fixture.haircut to 1)

        whenn()
        fixture.state.decreaseQuantity(fixture.beard)

        then()
        assertEquals(listOf("haircut" to 1), fixture.selection())
        assertEquals(0, fixture.state.replaceCount)
    }

    @Test
    fun `quantityOf returns the quantity of a picked item`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.select(fixture.haircut to 2)

        whenn()
        val result = fixture.state.quantityOf(fixture.haircut)

        then()
        assertEquals(2, result)
    }

    @Test
    fun `quantityOf returns zero for an unpicked item`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.select(fixture.haircut to 2)

        whenn()
        val result = fixture.state.quantityOf(fixture.beard)

        then()
        assertEquals(0, result)
    }

    @Test
    fun `pick appends only unpicked items with quantity one`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.select(fixture.haircut to 2)

        whenn()
        fixture.state.pick(listOf(fixture.haircut, fixture.beard, fixture.beard))

        then()
        assertEquals(listOf("haircut" to 2, "beard" to 1), fixture.selection())
    }

    @Test
    fun `unpick removes the items by picker item id`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.select(fixture.haircut to 2, fixture.beard to 1)

        whenn()
        fixture.state.unpick(listOf(fixture.haircut.copy(displayName = "Other".desc())))

        then()
        assertEquals(listOf("beard" to 1), fixture.selection())
    }

    @Test
    fun `replacePicked selects each item with quantity one`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.select(fixture.haircut to 3)

        whenn()
        fixture.state.replacePicked(listOf(fixture.beard, fixture.haircut))

        then()
        assertEquals(listOf("beard" to 1, "haircut" to 1), fixture.selection())
    }

    @Test
    fun `selectedUnits repeats each picked item by its quantity`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.select(fixture.haircut to 2, fixture.beard to 1)

        whenn()
        val result = fixture.state.selectedUnits()

        then()
        assertEquals(listOf(fixture.haircut, fixture.haircut, fixture.beard), result)
    }
}

private class QuantityTestPickedItem<T : PickerPresentation>(
    override val item: T,
    override var quantity: Int,
) : PickedItem<T>

private class QuantityTestPickerState<T : PickerPresentation> : OptionsMultiPickerState<T> {
    override var id: String = "picker"
    override var isVisible: Boolean = true
    override var pickerTitle: StringDesc = "".desc()
    override var pickerType: PickerFieldState.PickerType = PickerFieldState.PickerType.BOTTOM_SHEET
    override var placeholder: StringDesc? = null
    override val options: MutableList<T> = mutableListOf()
    override val selectedItems: MutableList<PickedItem<T>> = mutableListOf()
    override var onItemsPicked: (List<T>) -> Unit = {}
    override var onItemsRemoveRequested: (List<T>) -> Unit = {}
    override var onQuantityIncreaseRequested: (T) -> Unit = {}
    override var onQuantityDecreaseRequested: (T) -> Unit = {}
    override var addItemText: StringDesc = "".desc()
    override var isEditable: Boolean = true
    override var isQuantityEditable: Boolean = true
    var replaceCount = 0

    override fun refreshIdentity() = Unit

    override fun createPickedItem(item: T, quantity: Int): PickedItem<T> {
        return QuantityTestPickedItem(item, quantity)
    }

    override fun replaceOptions(options: List<T>) {
        this.options.clear()
        this.options += options
    }

    override fun replaceSelected(selected: List<PickedItem<T>>) {
        replaceCount++
        selectedItems.clear()
        selectedItems += selected
    }
}
