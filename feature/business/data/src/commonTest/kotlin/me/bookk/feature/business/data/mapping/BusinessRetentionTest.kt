package me.bookk.feature.business.data.mapping

import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.relation.BusinessLocal
import me.bookk.feature.business.data.remote.model.BusinessPermissionsRemote
import me.bookk.feature.business.data.remote.model.BusinessRemote
import me.bookk.feature.business.data.remote.model.ResourcePermissionRemote
import me.bookk.feature.business.data.remote.model.toRemote
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class BusinessRetentionTest {
    private fun business(): Business {
        val permissions = ResourcePermission(false, false, false)
        return Business(
            id = Uuid.parse("00000000-0000-0000-0000-000000000001"),
            name = "Business", description = "", address = "", location = null,
            currency = Currency("USD"), timeZone = TimeZone.UTC, socials = emptyMap(),
            schedule = WorkingSchedule(emptyMap(), emptyList()),
            permissions = BusinessPermissions(
                permissions,
                permissions,
                permissions,
                permissions,
                permissions,
            ),
        )
    }

    @Test
    fun `cached business retains its retention period`() = runUnitTest {
        given()
        val business = business().copy(clientDataRetentionMonths = 60)

        whenn()
        val restored = BusinessLocal(
            business.toLocal(),
            emptyList(),
            emptyList(),
            emptyList(),
        ).toDomain()

        then()
        assertEquals(60, restored.clientDataRetentionMonths)
    }

    @Test
    fun `domain business defaults to 36 months and keeps it in the cache`() = runUnitTest {
        given()
        val business = business()
        val defaultRetention: Int = business.clientDataRetentionMonths

        whenn()
        val restored = BusinessLocal(
            business.toLocal(),
            emptyList(),
            emptyList(),
            emptyList(),
        ).toDomain()

        then()
        assertEquals(36, defaultRetention)
        assertEquals(36, restored.clientDataRetentionMonths)
    }

    @Test
    fun `business update sends the known retention period`() = runUnitTest {
        given()
        val business = business().copy(clientDataRetentionMonths = 60)

        whenn()
        val remote = business.toRemote()

        then()
        assertEquals(60, remote.clientDataRetentionMonths)
    }

    @Test
    fun `maps the backend default retention to the domain`() = runUnitTest {
        given()
        val remote = remoteBusiness(36)

        whenn()
        val business = remote.toDomain()

        then()
        assertEquals(36, business.clientDataRetentionMonths)
    }

    @Test
    fun `preserves custom remote retention in the domain`() = runUnitTest {
        given()
        val remote = remoteBusiness(60)

        whenn()
        val business = remote.toDomain()

        then()
        assertEquals(60, business.clientDataRetentionMonths)
    }

    private fun remoteBusiness(months: Int): BusinessRemote {
        val business = business()
        val permission = ResourcePermissionRemote()
        return BusinessRemote(
            id = business.id,
            name = business.name,
            description = business.description,
            address = business.address,
            timeZone = business.timeZone,
            location = null,
            currencyCode = business.currency.code(),
            socials = emptyList(),
            schedule = business.schedule.toRemote(),
            permissions = BusinessPermissionsRemote(
                permission,
                permission,
                permission,
                permission,
                permission,
            ),
            ownerId = Uuid.NIL,
            employeeId = null,
            clientId = null,
            clientDataRetentionMonths = months,
        )
    }
}
