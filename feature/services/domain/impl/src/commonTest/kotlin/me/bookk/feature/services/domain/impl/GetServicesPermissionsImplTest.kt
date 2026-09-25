package me.bookk.feature.services.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.services.domain.api.entity.ServicesPermissions
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetServicesPermissionsImplTest {

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
        val observeUserBusinessesChanges = mock<ObserveUserBusinessesChanges>()
        val sut = GetServicesPermissionsImpl(observeUserBusinessesChanges)

        fun stubBusinesses(vararg businesses: Business) {
            every { observeUserBusinessesChanges.invoke() } returns flowOf(businesses.toList())
        }
    }

    @Test
    fun `allows editing when the user can update services of the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(stubBusiness(businessId, ResourcePermission(view = true, update = true)))

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(ServicesPermissions(canEdit = true, canDelete = false), result)
    }

    @Test
    fun `allows deleting when the user can delete services of the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(stubBusiness(businessId, ResourcePermission(view = true, delete = true)))

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(ServicesPermissions(canEdit = false, canDelete = true), result)
    }

    @Test
    fun `ignores service grants of a different business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(
            stubBusiness(Uuid.random(), ResourcePermission(view = true, update = true, delete = true)),
            stubBusiness(businessId, ResourcePermission(view = true))
        )

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(ServicesPermissions(canEdit = false, canDelete = false), result)
    }

    @Test
    fun `denies everything when the business is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubBusinesses()

        whenn()
        val result = fixture.sut(Uuid.random())

        then()
        assertEquals(ServicesPermissions(canEdit = false, canDelete = false), result)
    }

    private fun stubBusiness(id: Uuid, services: ResourcePermission) = Business(
        id = id,
        name = "Test Business",
        description = "",
        address = "",
        location = null,
        currency = Currency("USD"),
        timeZone = TimeZone.UTC,
        socials = emptyMap(),
        schedule = WorkingSchedule(),
        permissions = BusinessPermissions(
            business = ResourcePermission(),
            employees = ResourcePermission(),
            clients = ResourcePermission(),
            services = services,
            appointments = ResourcePermission()
        )
    )
}
