package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.AppointmentOptions
import me.bookk.feature.clients.domain.api.GetClientsList
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveAppointmentOptionsImplTest {

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
        val clients = MutableStateFlow<List<Client>>(emptyList())
        val services = MutableStateFlow<List<Service>>(emptyList())
        val getClients = mock<GetClientsList> {
            every { flow() } returns clients
        }
        val getServices = mock<GetServices> {
            every { flow() } returns services
        }
        val sut = ObserveAppointmentOptionsImpl(getClients, getServices)
    }

    @Test
    fun `emits snapshots of cached clients and services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val client = stubClient()
        val service = stubService()
        fixture.clients.value = listOf(client)
        fixture.services.value = listOf(service)
        val emitted = mutableListOf<AppointmentOptions>()

        whenn()
        val job = launch(testDispatcher) { fixture.sut().collect { emitted.add(it) } }

        then()
        job.cancel()
        val options = emitted.single()
        assertEquals(listOf(client.id), options.clients.map { it.id })
        assertEquals(client.fullName, options.clients.single().fullName)
        assertEquals(listOf(service.id), options.services.map { it.id })
        assertEquals(service.group.id, options.services.single().groupId)
    }

    @Test
    fun `emits updated options when clients change`() = runUnitTest {
        given()
        val fixture = Fixture()
        val emitted = mutableListOf<AppointmentOptions>()
        val job = launch(testDispatcher) { fixture.sut().collect { emitted.add(it) } }
        val client = stubClient()

        whenn()
        fixture.clients.value = listOf(client)

        then()
        job.cancel()
        assertEquals(listOf(client.id), emitted.last().clients.map { it.id })
    }

    @Test
    fun `emits updated options when services change`() = runUnitTest {
        given()
        val fixture = Fixture()
        val emitted = mutableListOf<AppointmentOptions>()
        val job = launch(testDispatcher) { fixture.sut().collect { emitted.add(it) } }
        val service = stubService()

        whenn()
        fixture.services.value = listOf(service)

        then()
        job.cancel()
        assertEquals(listOf(service.id), emitted.last().services.map { it.id })
    }
}
