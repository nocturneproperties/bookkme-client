package me.bookk.feature.business.domain.impl.business

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
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.impl.stubBusiness
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GetBusinessesImplTest {

    private class Fixture {
        val dataSource = mock<BusinessDataSource>()
        val sut = GetBusinessesImpl(dataSource)
    }

    @Test
    fun `flow emits the cached businesses with the given ids`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businesses = listOf(stubBusiness(), stubBusiness())
        val ids = businesses.map { it.id }.toSet()
        every { fixture.dataSource.observeBusinessesInDb(ids) } returns flowOf(businesses)

        whenn()
        val result = fixture.sut.flow(ids).first()

        then()
        assertEquals(businesses, result)
    }

    @Test
    fun `refresh saves the fetched businesses`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businesses = listOf(stubBusiness(), stubBusiness())
        val ids = businesses.map { it.id }.toSet()
        everySuspend { fixture.dataSource.getBusinessesByIdsFromRemote(ids) } returns businesses
        everySuspend { fixture.dataSource.saveBusinessListInDB(businesses) } returns Unit

        whenn()
        val result = fixture.sut.refresh(ids)

        then()
        assertEquals(businesses, result)
        verifySuspend { fixture.dataSource.saveBusinessListInDB(businesses) }
    }

    @Test
    fun `refresh without ids neither fetches nor saves`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut.refresh(emptySet())

        then()
        assertEquals(emptyList(), result)
        verifySuspend(VerifyMode.exactly(0)) {
            fixture.dataSource.getBusinessesByIdsFromRemote(any())
        }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveBusinessListInDB(any()) }
    }

    @Test
    fun `refresh keeps the cache when the fetch fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        val ids = setOf(stubBusiness().id)
        everySuspend { fixture.dataSource.getBusinessesByIdsFromRemote(ids) } throws
            IllegalStateException()

        whenn()
        assertFailsWith<IllegalStateException> { fixture.sut.refresh(ids) }

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveBusinessListInDB(any()) }
    }
}
