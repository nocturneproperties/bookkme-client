package me.bookk.feature.employees.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.entity.UserProfile
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.business.domain.api.entity.Business
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class IsBusinessOwnerImplTest {

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
        val currentUserId = Uuid.random()
        val observeUserBusinessesChanges = mock<ObserveUserBusinessesChanges>()
        val userProfileCRUD = mock<UserProfileCRUD> {
            everySuspend { get() } returns UserProfile(
                id = currentUserId,
                firstName = "Jane",
                lastName = "Doe",
                email = "jane@bookk.me"
            )
        }
        val sut = IsBusinessOwnerImpl(observeUserBusinessesChanges, userProfileCRUD)

        fun stubBusinesses(vararg businesses: Business) {
            every { observeUserBusinessesChanges.invoke() } returns flowOf(businesses.toList())
        }
    }

    @Test
    fun `returns true when the user owns the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val userId = Uuid.random()
        val businessId = Uuid.random()
        fixture.stubBusinesses(stubBusiness(id = businessId, ownerId = userId))

        whenn()
        val result = fixture.sut(userId, businessId)

        then()
        assertTrue(result)
    }

    @Test
    fun `returns false when another user owns the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(stubBusiness(id = businessId, ownerId = Uuid.random()))

        whenn()
        val result = fixture.sut(Uuid.random(), businessId)

        then()
        assertFalse(result)
    }

    @Test
    fun `returns false when the user owns a different business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val userId = Uuid.random()
        val businessId = Uuid.random()
        fixture.stubBusinesses(
            stubBusiness(id = Uuid.random(), ownerId = userId),
            stubBusiness(id = businessId, ownerId = Uuid.random())
        )

        whenn()
        val result = fixture.sut(userId, businessId)

        then()
        assertFalse(result)
    }

    @Test
    fun `returns false when the business is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubBusinesses()

        whenn()
        val result = fixture.sut(Uuid.random(), Uuid.random())

        then()
        assertFalse(result)
    }

    @Test
    fun `returns false when the cached business has no owner yet`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(stubBusiness(id = businessId, ownerId = null))

        whenn()
        val result = fixture.sut(Uuid.random(), businessId)

        then()
        assertFalse(result)
    }

    @Test
    fun `returns true when the current user owns the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(stubBusiness(id = businessId, ownerId = fixture.currentUserId))

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertTrue(result)
    }

    @Test
    fun `returns false when another user owns the current user business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(stubBusiness(id = businessId, ownerId = Uuid.random()))

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertFalse(result)
    }

    @Test
    fun `returns false for the current user when the business is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubBusinesses()

        whenn()
        val result = fixture.sut(Uuid.random())

        then()
        assertFalse(result)
    }
}
