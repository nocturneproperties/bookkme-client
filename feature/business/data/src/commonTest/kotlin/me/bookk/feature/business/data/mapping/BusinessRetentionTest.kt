package me.bookk.feature.business.data.mapping

import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.relation.BusinessLocal
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class BusinessRetentionTest {
    private fun business(months: Int?): Business {
        val permissions = ResourcePermission(false, false, false)
        return Business(
            id = Uuid.parse("00000000-0000-0000-0000-000000000001"),
            name = "Business", description = "", address = "", location = null,
            currency = Currency("USD"), timeZone = TimeZone.UTC, socials = emptyMap(),
            schedule = WorkingSchedule(emptyMap(), emptyList()),
            permissions = BusinessPermissions(permissions, permissions, permissions, permissions, permissions),
            clientDataRetentionMonths = months
        )
    }

    @Test
    fun `cached business retains its retention period`() = runUnitTest {
        given()
        val business = business(36)

        whenn()
        val restored = BusinessLocal(business.toLocal(), emptyList(), emptyList(), emptyList()).toDomain()

        then()
        assertEquals(36, restored.clientDataRetentionMonths)
    }

    @Test
    fun `an older cached business has unknown retention`() = runUnitTest {
        given()
        val business = business(null)

        whenn()
        val restored = BusinessLocal(business.toLocal(), emptyList(), emptyList(), emptyList()).toDomain()

        then()
        assertNull(restored.clientDataRetentionMonths)
    }

    @Test
    fun `business update sends the known retention period`() = runUnitTest {
        given()
        val business = business(60)

        whenn()
        val remote = business.toRemote()

        then()
        assertEquals(60, remote.clientDataRetentionMonths)
    }
}
