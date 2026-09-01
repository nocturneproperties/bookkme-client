package me.bookk.feature.services.domain.impl.quote

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.services.domain.api.quote.CreateQuote
import me.bookk.feature.services.domain.api.quote.entity.Quote
import me.bookk.feature.services.domain.datasource.QuoteDataSource
import me.bookk.feature.services.domain.datasource.ServiceErrorCodes
import me.bookk.feature.services.domain.impl.stubService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class CreateQuoteImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture {
        val dataSource = mock<QuoteDataSource>()
        val sut = CreateQuoteImpl(dataSource)
    }

    private fun stubQuote(businessId: Uuid = Uuid.random()) = Quote(
        id = Uuid.random(),
        services = listOf(stubService(businessId)),
        token = "signed-token"
    )

    @Test
    fun `returns created quote from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val serviceIds = listOf(Uuid.random())
        val quote = stubQuote(businessId)
        everySuspend { fixture.dataSource.createQuote(businessId, serviceIds) } returns quote

        whenn()
        val result = fixture.sut(businessId, serviceIds)

        then()
        assertEquals(quote, result)
    }

    @Test
    fun `throws ServiceNotFound on BUSINESS_QUOTE_SERVICE_NOT_FOUND error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val serviceIds = listOf(Uuid.random())
        everySuspend { fixture.dataSource.createQuote(businessId, serviceIds) } throws
            DomainError.BusinessError(ServiceErrorCodes.BUSINESS_QUOTE_SERVICE_NOT_FOUND, "msg")

        whenn()
        then()
        assertFailsWith<CreateQuote.Error.ServiceNotFound> {
            fixture.sut(businessId, serviceIds)
        }
    }
}
