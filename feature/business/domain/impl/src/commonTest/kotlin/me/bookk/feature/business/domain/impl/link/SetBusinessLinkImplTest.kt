package me.bookk.feature.business.domain.impl.link

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.entity.BusinessLink
import me.bookk.feature.business.domain.api.link.SetBusinessLink
import me.bookk.feature.business.domain.datasource.BusinessErrorCodes
import me.bookk.feature.business.domain.datasource.BusinessLinkDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class SetBusinessLinkImplTest {

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
        val dataSource = mock<BusinessLinkDataSource> {
            everySuspend { saveLinkInDb(any()) } returns Unit
        }
        val sut = SetBusinessLinkImpl(dataSource)
    }

    @Test
    fun `returns the saved link`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val link = BusinessLink(businessId, "my-salon")
        everySuspend { fixture.dataSource.setLink(businessId, "my-salon") } returns link

        whenn()
        val result = fixture.sut(businessId, "my-salon")

        then()
        assertEquals(link, result)
    }

    @Test
    fun `saves the link locally once the server accepts it`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val link = BusinessLink(businessId, "my-salon")
        everySuspend { fixture.dataSource.setLink(businessId, "my-salon") } returns link

        whenn()
        fixture.sut(businessId, "my-salon")

        then()
        verifySuspend { fixture.dataSource.saveLinkInDb(link) }
    }

    @Test
    fun `keeps the saved link when the server rejects the name`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failsWith(DomainError.BusinessError(BusinessErrorCodes.BUSINESS_LINK_TAKEN, ""))

        whenn()
        runCatching { fixture.sut(Uuid.random(), NAME) }

        then()
        verifySuspend(VerifyMode.not) { fixture.dataSource.saveLinkInDb(any()) }
    }

    @Test
    fun `sends the name trimmed and in lowercase`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.setLink(businessId, "my-salon-2") } returns
            BusinessLink(businessId, "my-salon-2")

        whenn()
        fixture.sut(businessId, "  My-Salon-2 ")

        then()
        verifySuspend { fixture.dataSource.setLink(businessId, "my-salon-2") }
    }

    @Test
    fun `throws Invalid without a request when the name is shorter than 3 characters`() =
        runUnitTest {
            given()
            val fixture = Fixture()

            whenn()
            then()
            assertFailsWith<SetBusinessLink.Error.Invalid> { fixture.sut(Uuid.random(), "ab") }
            verifySuspend(VerifyMode.not) { fixture.dataSource.setLink(any(), any()) }
        }

    @Test
    fun `accepts names of 3 and 32 characters`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val shortest = "abc"
        val longest = "a".repeat(32)
        everySuspend { fixture.dataSource.setLink(businessId, any()) } returns
            BusinessLink(businessId, shortest)

        whenn()
        fixture.sut(businessId, shortest)
        fixture.sut(businessId, longest)

        then()
        verifySuspend { fixture.dataSource.setLink(businessId, shortest) }
        verifySuspend { fixture.dataSource.setLink(businessId, longest) }
    }

    @Test
    fun `throws Invalid when the name is longer than 32 characters`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        then()
        assertFailsWith<SetBusinessLink.Error.Invalid> {
            fixture.sut(Uuid.random(), "a".repeat(33))
        }
    }

    @Test
    fun `throws Invalid when the name has characters other than latin letters digits and hyphens`() =
        runUnitTest {
            given()
            val fixture = Fixture()

            whenn()
            then()
            listOf("my salon", "my_salon", "салон", "my.salon").forEach {
                assertFailsWith<SetBusinessLink.Error.Invalid> { fixture.sut(Uuid.random(), it) }
            }
        }

    @Test
    fun `throws Invalid when hyphens are leading trailing or doubled`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        then()
        listOf("-salon", "salon-", "my--salon").forEach {
            assertFailsWith<SetBusinessLink.Error.Invalid> { fixture.sut(Uuid.random(), it) }
        }
    }

    @Test
    fun `throws Invalid on BUSINESS_LINK_INVALID`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failsWith(DomainError.BusinessError(BusinessErrorCodes.BUSINESS_LINK_INVALID, ""))

        whenn()
        then()
        assertFailsWith<SetBusinessLink.Error.Invalid> { fixture.sut(Uuid.random(), NAME) }
    }

    @Test
    fun `throws Reserved on BUSINESS_LINK_RESERVED`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failsWith(DomainError.BusinessError(BusinessErrorCodes.BUSINESS_LINK_RESERVED, ""))

        whenn()
        then()
        assertFailsWith<SetBusinessLink.Error.Reserved> { fixture.sut(Uuid.random(), NAME) }
    }

    @Test
    fun `throws Taken on BUSINESS_LINK_TAKEN`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failsWith(DomainError.BusinessError(BusinessErrorCodes.BUSINESS_LINK_TAKEN, ""))

        whenn()
        then()
        assertFailsWith<SetBusinessLink.Error.Taken> { fixture.sut(Uuid.random(), NAME) }
    }

    @Test
    fun `throws ChangeLimitReached on BUSINESS_LINK_CHANGE_LIMIT_REACHED`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failsWith(
            DomainError.BusinessError(BusinessErrorCodes.BUSINESS_LINK_CHANGE_LIMIT_REACHED, ""),
        )

        whenn()
        then()
        assertFailsWith<SetBusinessLink.Error.ChangeLimitReached> {
            fixture.sut(Uuid.random(), NAME)
        }
    }

    @Test
    fun `throws NotOwner when the business is not found for the caller`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failsWith(DomainError.NotFound(IllegalStateException("404")))

        whenn()
        then()
        assertFailsWith<SetBusinessLink.Error.NotOwner> { fixture.sut(Uuid.random(), NAME) }
    }

    @Test
    fun `rethrows other business errors`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failsWith(DomainError.BusinessError(UNKNOWN_ERROR_CODE, ""))

        whenn()
        then()
        assertFailsWith<DomainError.BusinessError> { fixture.sut(Uuid.random(), NAME) }
    }

    private fun Fixture.failsWith(error: Throwable) {
        everySuspend { dataSource.setLink(any(), any()) } throws error
    }

    private companion object {
        const val NAME = "my-salon"
        const val UNKNOWN_ERROR_CODE = 999_999
    }
}
