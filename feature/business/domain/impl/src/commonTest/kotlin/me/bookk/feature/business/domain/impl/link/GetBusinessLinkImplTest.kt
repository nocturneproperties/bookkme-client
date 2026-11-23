package me.bookk.feature.business.domain.impl.link

import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.entity.BusinessLink
import me.bookk.feature.business.domain.api.link.GetBusinessLink
import me.bookk.feature.business.domain.datasource.BusinessErrorCodes
import me.bookk.feature.business.domain.datasource.BusinessLinkDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class GetBusinessLinkImplTest {

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
        val businessId = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val cached = MutableStateFlow<BusinessLink?>(null)
        val dataSource = mock<BusinessLinkDataSource> {
            every { observeLink(businessId) } returns cached
            everySuspend { saveLinkInDb(any()) } calls { (link: BusinessLink) ->
                cached.value = link
            }
            everySuspend { deleteLinkFromDb(businessId) } calls { cached.value = null }
        }
        val sut = GetBusinessLinkImpl(dataSource)

        fun remoteFailsWith(error: Throwable) {
            everySuspend { dataSource.getLink(businessId) } throws error
        }
    }

    @Test
    fun `emits the saved link without using the network`() = runUnitTest {
        given()
        val fixture = Fixture()
        val link = BusinessLink(fixture.businessId, "my-salon")
        fixture.cached.value = link

        whenn()
        val result = fixture.sut.flow(fixture.businessId).first()

        then()
        assertEquals(link, result)
        verifySuspend(VerifyMode.not) { fixture.dataSource.getLink(any()) }
    }

    @Test
    fun `refresh saves the server link locally`() = runUnitTest {
        given()
        val fixture = Fixture()
        val link = BusinessLink(fixture.businessId, "my-salon")
        everySuspend { fixture.dataSource.getLink(fixture.businessId) } returns link

        whenn()
        fixture.sut.refresh(fixture.businessId)

        then()
        assertEquals(link, fixture.cached.value)
    }

    @Test
    fun `refresh removes the saved link when the business has no link`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cached.value = BusinessLink(fixture.businessId, "old-name")
        fixture.remoteFailsWith(
            DomainError.BusinessError(BusinessErrorCodes.BUSINESS_LINK_NOT_FOUND, "msg"),
        )

        whenn()
        fixture.sut.refresh(fixture.businessId)

        then()
        assertNull(fixture.cached.value)
    }

    @Test
    fun `refresh forgets the saved link and throws NotOwner when the business is not found`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.cached.value = BusinessLink(fixture.businessId, "my-salon")
            fixture.remoteFailsWith(DomainError.NotFound(IllegalStateException("404")))

            whenn()
            val result = runCatching { fixture.sut.refresh(fixture.businessId) }

            then()
            assertFailsWith<GetBusinessLink.Error.NotOwner> { result.getOrThrow() }
            assertNull(fixture.cached.value)
        }

    @Test
    fun `failed refresh keeps the saved link and rethrows`() = runUnitTest {
        given()
        val fixture = Fixture()
        val link = BusinessLink(fixture.businessId, "my-salon")
        fixture.cached.value = link
        fixture.remoteFailsWith(DomainError.BusinessError(UNKNOWN_ERROR_CODE, "msg"))

        whenn()
        val result = runCatching { fixture.sut.refresh(fixture.businessId) }

        then()
        assertFailsWith<DomainError.BusinessError> { result.getOrThrow() }
        assertEquals(link, fixture.cached.value)
    }

    private companion object {
        const val UNKNOWN_ERROR_CODE = 999_999
    }
}
