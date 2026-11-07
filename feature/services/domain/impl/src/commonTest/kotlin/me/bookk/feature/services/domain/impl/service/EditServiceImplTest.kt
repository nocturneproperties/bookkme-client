package me.bookk.feature.services.domain.impl.service

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
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
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import me.bookk.feature.services.domain.impl.stubService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class EditServiceImplTest {

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
        val dataSource = mock<ServiceDataSource>()
        val sut = EditServiceImpl(dataSource)
    }

    @Test
    fun `returns edited service from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = stubService()
        val updated = service.copy(name = "Color")
        everySuspend { fixture.dataSource.editService(service) } returns updated
        everySuspend { fixture.dataSource.saveServiceInDB(updated) } returns Unit

        whenn()
        val result = fixture.sut(service)

        then()
        assertEquals(updated, result)
    }

    @Test
    fun `saves edited service in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = stubService()
        val updated = service.copy(name = "Color")
        everySuspend { fixture.dataSource.editService(service) } returns updated
        everySuspend { fixture.dataSource.saveServiceInDB(updated) } returns Unit

        whenn()
        fixture.sut(service)

        then()
        verifySuspend { fixture.dataSource.saveServiceInDB(updated) }
    }

    @Test
    fun `sends trimmed name to backend`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = stubService().copy(name = "  Haircut \n")
        val trimmed = input.copy(name = "Haircut")
        everySuspend { fixture.dataSource.editService(trimmed) } returns trimmed
        everySuspend { fixture.dataSource.saveServiceInDB(trimmed) } returns Unit

        whenn()
        fixture.sut(input)

        then()
        verifySuspend { fixture.dataSource.editService(trimmed) }
    }

    @Test
    fun `maps backend error 200007 to NameAlreadyExists`() = runUnitTest {
        given()
        val fixture = Fixture()

        everySuspend { fixture.dataSource.editService(any()) } throws
            me.bookk.core.domain.entity.Error.BusinessError(200007, "rejected")

        whenn()
        val result = runCatching { fixture.sut(stubService()) }

        then()
        assertFailsWith<
            me.bookk.feature.services.domain.api.service.EditService.Error.NameAlreadyExists,
            > {
            result.getOrThrow()
        }
    }

    @Test
    fun `maps backend error 200008 to InvalidName`() = runUnitTest {
        given()
        val fixture = Fixture()

        everySuspend { fixture.dataSource.editService(any()) } throws
            me.bookk.core.domain.entity.Error.BusinessError(200008, "rejected")

        whenn()
        val result = runCatching { fixture.sut(stubService()) }

        then()
        assertFailsWith<
            me.bookk.feature.services.domain.api.service.EditService.Error.InvalidName,
            > {
            result.getOrThrow()
        }
    }

    @Test
    fun `maps backend error 200012 to GroupNotFound`() = runUnitTest {
        given()
        val fixture = Fixture()

        everySuspend { fixture.dataSource.editService(any()) } throws
            me.bookk.core.domain.entity.Error.BusinessError(200012, "rejected")

        whenn()
        val result = runCatching { fixture.sut(stubService()) }

        then()
        assertFailsWith<
            me.bookk.feature.services.domain.api.service.EditService.Error.GroupNotFound,
            > {
            result.getOrThrow()
        }
    }

    @Test
    fun `maps backend error 200037 to InvalidPricePrecision`() = runUnitTest {
        given()
        val fixture = Fixture()

        everySuspend { fixture.dataSource.editService(any()) } throws
            me.bookk.core.domain.entity.Error.BusinessError(200037, "rejected")

        whenn()
        val result = runCatching { fixture.sut(stubService()) }

        then()
        assertFailsWith<
            me.bookk.feature.services.domain.api.service.EditService.Error.InvalidPricePrecision,
            > {
            result.getOrThrow()
        }
    }

    @Test
    fun `maps backend error 200038 to InvalidBusinessCurrency`() = runUnitTest {
        given()
        val fixture = Fixture()

        everySuspend { fixture.dataSource.editService(any()) } throws
            me.bookk.core.domain.entity.Error.BusinessError(200038, "rejected")

        whenn()
        val result = runCatching { fixture.sut(stubService()) }

        then()
        assertFailsWith<
            me.bookk.feature.services.domain.api.service.EditService.Error.InvalidBusinessCurrency,
            > {
            result.getOrThrow()
        }
    }
}
