package me.bookk.feature.booking.data.datasource

import kotlinx.coroutines.flow.first
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.booking.domain.datasource.BookingSelection
import me.bookk.feature.booking.domain.datasource.BookingSelection.SelectedService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class InMemoryBookingPreparationDataSourceTest {

    private val businessId = Uuid.random()
    private val picked = BookingSelection(listOf(SelectedService(Uuid.random(), 2)))

    @Test
    fun `has nothing saved for a business initially`() = runUnitTest {
        given()
        val sut = InMemoryBookingPreparationDataSource()

        whenn()
        val selection = sut.observeSelection(businessId).first()

        then()
        assertEquals(BookingSelection(), selection)
    }

    @Test
    fun `emits the saved selection of the business`() = runUnitTest {
        given()
        val sut = InMemoryBookingPreparationDataSource()

        whenn()
        sut.saveSelection(businessId, picked)

        then()
        assertEquals(picked, sut.observeSelection(businessId).first())
    }

    @Test
    fun `replaces the selection saved before`() = runUnitTest {
        given()
        val sut = InMemoryBookingPreparationDataSource()
        sut.saveSelection(businessId, picked)
        val replacement = BookingSelection(listOf(SelectedService(Uuid.random(), 1)))

        whenn()
        sut.saveSelection(businessId, replacement)

        then()
        assertEquals(replacement, sut.observeSelection(businessId).first())
    }

    @Test
    fun `keeps the selection of each business apart`() = runUnitTest {
        given()
        val sut = InMemoryBookingPreparationDataSource()
        val otherBusinessId = Uuid.random()

        whenn()
        sut.saveSelection(otherBusinessId, picked)

        then()
        assertEquals(BookingSelection(), sut.observeSelection(businessId).first())
    }

    @Test
    fun `forgets every business's selection on logout`() = runUnitTest {
        given()
        val sut = InMemoryBookingPreparationDataSource()
        sut.saveSelection(businessId, picked)

        whenn()
        sut.doOnLogOut()

        then()
        assertEquals(BookingSelection(), sut.observeSelection(businessId).first())
    }
}
