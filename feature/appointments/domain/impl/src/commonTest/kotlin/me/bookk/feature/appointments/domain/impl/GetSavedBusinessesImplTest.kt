package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.SavedBusiness
import me.bookk.feature.appointments.domain.api.entity.SavedBusinessEntry
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import me.bookk.feature.business.domain.api.business.GetBusinesses
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant
import kotlin.uuid.Uuid

class GetSavedBusinessesImplTest {

    private class Fixture {
        val dataSource = mock<ClientBookingDataSource>()
        val getBusinesses = mock<GetBusinesses>()
        val sut = GetSavedBusinessesImpl(dataSource, getBusinesses)
    }

    private fun entry(businessId: Uuid = Uuid.random(), lastVisitAt: Instant? = null) =
        SavedBusinessEntry(
            businessId = businessId,
            isEnabled = true,
            savedAt = Instant.fromEpochMilliseconds(1_000),
            lastVisitAt = lastVisitAt,
        )

    @Test
    fun `flow names cached saved businesses in saved order`() = runUnitTest {
        given()
        val fixture = Fixture()
        val visited = entry(lastVisitAt = Instant.fromEpochMilliseconds(500))
        val newer = entry()
        every { fixture.dataSource.observeSavedBusinesses() } returns flowOf(listOf(newer, visited))
        every { fixture.getBusinesses.flow(setOf(newer.businessId, visited.businessId)) } returns
            flowOf(
                listOf(
                    stubBusiness(id = visited.businessId).copy(name = "Studio"),
                    stubBusiness(id = newer.businessId).copy(name = "Nail Room"),
                ),
            )

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(
            listOf(
                SavedBusiness(newer.businessId, "Nail Room", true, newer.savedAt, null),
                SavedBusiness(
                    visited.businessId,
                    "Studio",
                    true,
                    visited.savedAt,
                    visited.lastVisitAt,
                ),
            ),
            result,
        )
    }

    @Test
    fun `flow leaves the name empty while the business is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        val saved = entry()
        every { fixture.dataSource.observeSavedBusinesses() } returns flowOf(listOf(saved))
        every { fixture.getBusinesses.flow(setOf(saved.businessId)) } returns flowOf(emptyList())

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals("", result.single().name)
    }

    @Test
    fun `refresh refreshes the saved businesses before saving the list`() = runUnitTest {
        given()
        val fixture = Fixture()
        val saved = entry()
        everySuspend { fixture.dataSource.getSavedBusinesses() } returns listOf(saved)
        everySuspend { fixture.getBusinesses.refresh(setOf(saved.businessId)) } returns
            listOf(stubBusiness(id = saved.businessId).copy(name = "Studio"))
        everySuspend { fixture.dataSource.saveSavedBusinessesInDb(any()) } returns Unit

        whenn()
        val result = fixture.sut.refresh()

        then()
        verifySuspend(VerifyMode.order) {
            fixture.getBusinesses.refresh(setOf(saved.businessId))
            fixture.dataSource.saveSavedBusinessesInDb(listOf(saved))
        }
        assertEquals(
            listOf(SavedBusiness(saved.businessId, "Studio", true, saved.savedAt, null)),
            result,
        )
    }

    @Test
    fun `refresh keeps the cache when the list cannot be fetched`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getSavedBusinesses() } throws IllegalStateException()

        whenn()
        assertFailsWith<IllegalStateException> { fixture.sut.refresh() }

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveSavedBusinessesInDb(any()) }
    }

    @Test
    fun `refresh keeps the cache when the businesses cannot be fetched`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getSavedBusinesses() } returns listOf(entry())
        everySuspend { fixture.getBusinesses.refresh(any()) } throws IllegalStateException()

        whenn()
        assertFailsWith<IllegalStateException> { fixture.sut.refresh() }

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveSavedBusinessesInDb(any()) }
    }
}
