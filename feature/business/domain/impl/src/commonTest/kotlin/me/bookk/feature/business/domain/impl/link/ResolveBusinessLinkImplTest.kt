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
import me.bookk.feature.business.domain.api.link.ResolveBusinessLink
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.datasource.BusinessErrorCodes
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class ResolveBusinessLinkImplTest {

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
        val link = BusinessLink(Uuid.parse("00000000-0000-0000-0000-000000000001"), "my-salon")
        val dataSource = mock<BusinessDataSource> {
            everySuspend { resolveLink(any()) } returns link
        }
        val sut = ResolveBusinessLinkImpl(dataSource)
    }

    @Test
    fun `returns the business link the backend resolves`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut("my-salon")

        then()
        assertEquals(fixture.link, result)
    }

    @Test
    fun `looks the name up trimmed and lowercased`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut(" My-Salon ")

        then()
        verifySuspend { fixture.dataSource.resolveLink("my-salon") }
    }

    @Test
    fun `throws not found without a request when the name is not a valid link name`() =
        runUnitTest {
            given()
            val fixture = Fixture()

            whenn()
            val result = runCatching { fixture.sut("x") }

            then()
            assertFailsWith<ResolveBusinessLink.Error.NotFound> { result.getOrThrow() }
            verifySuspend(VerifyMode.not) { fixture.dataSource.resolveLink(any()) }
        }

    @Test
    fun `throws not found when no business has the link`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.resolveLink(any()) } throws
            DomainError.BusinessError(BusinessErrorCodes.BUSINESS_LINK_NOT_FOUND, "")

        whenn()
        val result = runCatching { fixture.sut("my-salon") }

        then()
        assertFailsWith<ResolveBusinessLink.Error.NotFound> { result.getOrThrow() }
    }

    @Test
    fun `passes other failures through`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.resolveLink(any()) } throws
            DomainError.NoConnectionError(Exception())

        whenn()
        val result = runCatching { fixture.sut("my-salon") }

        then()
        assertFailsWith<DomainError.NoConnectionError> { result.getOrThrow() }
    }
}
