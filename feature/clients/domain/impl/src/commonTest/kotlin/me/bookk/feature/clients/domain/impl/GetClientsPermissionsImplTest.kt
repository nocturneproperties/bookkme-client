package me.bookk.feature.clients.domain.impl

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
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.UserProfile
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.clients.domain.api.entity.ClientsPermissions
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetClientsPermissionsImplTest {

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
        val currentUserId = Uuid.random()
        val userProfileCRUD = mock<UserProfileCRUD> {
            every { observe() } returns flowOf(
                UserProfile(
                    id = currentUserId,
                    firstName = "Jane",
                    lastName = "Smith",
                    email = "jane@example.com",
                ),
            )
        }
        val sut = GetClientsPermissionsImpl(observeUserBusinessesChanges, userProfileCRUD)

        fun stubBusinesses(vararg businesses: Business) {
            every { observeUserBusinessesChanges.invoke() } returns flowOf(businesses.toList())
        }
    }

    @Test
    fun `allows editing when the user can update clients of the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(
            stubBusiness(businessId, ResourcePermission(view = true, update = true)),
        )

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(
            ClientsPermissions(
                canEdit = true,
                canDeleteDetached = false,
                canDeleteIntegrated = false,
            ),
            result,
        )
    }

    @Test
    fun `allows deleting when the user can delete clients of the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(
            stubBusiness(businessId, ResourcePermission(view = true, delete = true)),
        )

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(
            ClientsPermissions(
                canEdit = false,
                canDeleteDetached = true,
                canDeleteIntegrated = false,
            ),
            result,
        )
    }

    @Test
    fun `ignores client grants of a different business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(
            stubBusiness(
                Uuid.random(),
                ResourcePermission(view = true, update = true, delete = true),
            ),
            stubBusiness(businessId, ResourcePermission(view = true)),
        )

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(
            ClientsPermissions(
                canEdit = false,
                canDeleteDetached = false,
                canDeleteIntegrated = false,
            ),
            result,
        )
    }

    @Test
    fun `denies everything when the business is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubBusinesses()

        whenn()
        val result = fixture.sut(Uuid.random())

        then()
        assertEquals(
            ClientsPermissions(
                canEdit = false,
                canDeleteDetached = false,
                canDeleteIntegrated = false,
            ),
            result,
        )
    }

    @Test
    fun `allows integrated client deletion for the business owner`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(
            stubBusiness(businessId, ResourcePermission(delete = true))
                .copy(ownerId = fixture.currentUserId),
        )

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(
            ClientsPermissions(
                canEdit = false,
                canDeleteDetached = true,
                canDeleteIntegrated = true,
            ),
            result,
        )
    }

    @Test
    fun `allows owner integrated deletion without a delegated delete grant`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(
            stubBusiness(businessId, ResourcePermission())
                .copy(ownerId = fixture.currentUserId),
        )

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(
            ClientsPermissions(
                canEdit = false,
                canDeleteDetached = false,
                canDeleteIntegrated = true,
            ),
            result,
        )
    }

    @Test
    fun `denies integrated client deletion to employees with delete permission`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(
            stubBusiness(businessId, ResourcePermission(delete = true))
                .copy(ownerId = Uuid.random()),
        )

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(
            ClientsPermissions(
                canEdit = false,
                canDeleteDetached = true,
                canDeleteIntegrated = false,
            ),
            result,
        )
    }

    @Test
    fun `ownership of another business does not allow integrated client deletion`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(
            stubBusiness(Uuid.random(), ResourcePermission(delete = true))
                .copy(ownerId = fixture.currentUserId),
            stubBusiness(businessId, ResourcePermission(delete = true))
                .copy(ownerId = Uuid.random()),
        )

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(
            ClientsPermissions(
                canEdit = false,
                canDeleteDetached = true,
                canDeleteIntegrated = false,
            ),
            result,
        )
    }

    @Test
    fun `missing cached owner denies integrated deletion while preserving detached deletion`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val businessId = Uuid.random()
            fixture.stubBusinesses(stubBusiness(businessId, ResourcePermission(delete = true)))

            whenn()
            val result = fixture.sut(businessId)

            then()
            assertEquals(
                ClientsPermissions(
                    canEdit = false,
                    canDeleteDetached = true,
                    canDeleteIntegrated = false,
                ),
                result,
            )
        }

    @Test
    fun `missing cached user profile denies integrated deletion`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(
            stubBusiness(businessId, ResourcePermission(delete = true))
                .copy(ownerId = fixture.currentUserId),
        )
        every { fixture.userProfileCRUD.observe() } returns flowOf(null)

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(
            ClientsPermissions(
                canEdit = false,
                canDeleteDetached = true,
                canDeleteIntegrated = false,
            ),
            result,
        )
    }

    private fun stubBusiness(id: Uuid, clients: ResourcePermission): Business {
        return Business(
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
                clients = clients,
                services = ResourcePermission(),
                appointments = ResourcePermission(),
            ),
        )
    }
}
