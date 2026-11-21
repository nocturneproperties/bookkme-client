package me.bookk.feature.appointments.data.mapping

import library.money.api.Money
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.data.remote.model.AdditionalServiceRemote
import me.bookk.feature.appointments.data.remote.model.PriceAdjustmentRemote
import me.bookk.feature.appointments.data.remote.model.RequestedServiceRemote
import me.bookk.feature.appointments.data.remote.model.ServiceSnapshotRemote
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustmentDraft
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class PriceAdjustmentMappingTest {

    private class Fixture {
        val price = Money(2550L, Money.SupportedCurrency.USD)
        val polish = remoteService("Polish")
        val massage = remoteService("Massage")

        private fun remoteService(name: String) = ServiceSnapshotRemote(
            id = Uuid.random(),
            name = name,
            groupId = Uuid.random(),
            price = Money(1000L, Money.SupportedCurrency.USD),
            duration = 30.minutes,
        )
    }

    @Test
    fun `repeats each additional service by its count`() = runUnitTest {
        given()
        val fixture = Fixture()
        val remote = PriceAdjustmentRemote(
            price = fixture.price,
            reason = "Extra",
            additionalServices = listOf(
                AdditionalServiceRemote(fixture.polish, 2),
                AdditionalServiceRemote(fixture.massage, 1),
            ),
        )

        whenn()
        val result = remote.toDomain()

        then()
        assertEquals(
            listOf(fixture.polish, fixture.polish, fixture.massage).map { it.toDomain() },
            result.additionalServices,
        )
    }

    @Test
    fun `groups repeated additional service ids into counts in first-picked order`() = runUnitTest {
        given()
        val fixture = Fixture()
        val draft = PriceAdjustmentDraft(
            additionalServiceIds = listOf(
                fixture.massage.id,
                fixture.polish.id,
                fixture.massage.id,
            ),
            price = fixture.price,
            reason = null,
        )

        whenn()
        val result = draft.toCompleteRequestRemote()

        then()
        assertEquals(
            listOf(
                RequestedServiceRemote(fixture.massage.id, 2),
                RequestedServiceRemote(fixture.polish.id, 1),
            ),
            result.priceAdjustment?.additionalServices,
        )
    }

    @Test
    fun `sends no price adjustment without a draft`() = runUnitTest {
        given()
        val draft: PriceAdjustmentDraft? = null

        whenn()
        val result = draft.toCompleteRequestRemote()

        then()
        assertNull(result.priceAdjustment)
    }
}
