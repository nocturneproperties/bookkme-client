package me.bookk.feature.business.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.entity.BusinessLink
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class CommonBusinessLinkCacheTest {
    private val clients = mutableListOf<HttpClient>()
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        clients.forEach { it.close() }
        Dispatchers.resetMain()
    }

    private inner class Fixture {
        val client = HttpClient(MockEngine { error("Cache operations must not use the network") })
            .also { clients.add(it) }
        val dao = FakeBusinessLinkDao()
        val sut = CommonBusinessLinkDataSource(client, dao)
        val first = BusinessLink(Uuid.parse("00000000-0000-0000-0000-000000000001"), "first")
        val second = BusinessLink(Uuid.parse("00000000-0000-0000-0000-000000000002"), "second")
    }

    @Test
    fun `missing link emits null without using the network`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut.observeLink(fixture.first.businessId).first()

        then()
        assertNull(result)
    }

    @Test
    fun `restores the saved link after datasource recreation`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.saveLinkInDb(fixture.first)
        val recreated = CommonBusinessLinkDataSource(fixture.client, fixture.dao)

        whenn()
        val result = recreated.observeLink(fixture.first.businessId).first()

        then()
        assertEquals(fixture.first, result)
    }

    @Test
    fun `saving replaces the link and notifies active observers`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.saveLinkInDb(fixture.first)
        val renamed = fixture.first.copy(name = "renamed")
        val emissions = mutableListOf<BusinessLink?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            fixture.sut.observeLink(fixture.first.businessId).collect { emissions.add(it) }
        }

        whenn()
        fixture.sut.saveLinkInDb(renamed)

        then()
        assertEquals(listOf<BusinessLink?>(fixture.first, renamed), emissions)
    }

    @Test
    fun `deleting a link keeps the links of other businesses`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.saveLinkInDb(fixture.first)
        fixture.sut.saveLinkInDb(fixture.second)

        whenn()
        fixture.sut.deleteLinkFromDb(fixture.first.businessId)

        then()
        assertNull(fixture.sut.observeLink(fixture.first.businessId).first())
        assertEquals(fixture.second, fixture.sut.observeLink(fixture.second.businessId).first())
    }

    @Test
    fun `logout clears the links of every business`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.saveLinkInDb(fixture.first)
        fixture.sut.saveLinkInDb(fixture.second)

        whenn()
        fixture.sut.doOnLogOut()

        then()
        assertNull(fixture.sut.observeLink(fixture.first.businessId).first())
        assertNull(fixture.sut.observeLink(fixture.second.businessId).first())
    }
}
