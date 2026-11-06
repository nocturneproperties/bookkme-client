package me.bookk.feature.appointments.data.mapping

import kotlinx.datetime.LocalDateTime
import library.money.api.Money
import me.bookk.core.data.dataSerializer
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.relation.AppointmentLocal
import me.bookk.feature.appointments.data.remote.model.AppointmentStatusRemote
import me.bookk.feature.appointments.data.remote.model.RequestedServiceRemote
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentCompletedBy
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustment
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class AppointmentMapperTest {

    private fun service(name: String) = ServiceSnapshot(
        id = Uuid.random(),
        name = name,
        groupId = Uuid.random(),
        price = Money(50.0, Money.SupportedCurrency.USD),
        duration = 30.minutes,
    )

    private fun appointment(services: List<ServiceSnapshot>) = Appointment.stub(
        date = LocalDateTime(2026, 10, 3, 10, 0),
    ).copy(services = services)

    private fun Appointment.toLocal() = AppointmentLocal(
        entity = toEntity(),
        services = toServiceEntities(),
        adjustmentServices = toAdjustmentServiceEntities(),
    )

    @Test
    fun `cached appointment preserves the linked client user`() = runUnitTest {
        given()
        val original = appointment(emptyList())
        val linked = original.copy(
            client = original.client.copy(
                userId = Uuid.parse("00000000-0000-0000-0000-000000000001"),
            ),
        )

        whenn()
        val restored = linked.toLocal().toDomain()

        then()
        assertEquals(linked.client.userId, restored.client.userId)
    }

    @Test
    fun `update request groups repeated service snapshots into counts in booking order`() =
        runUnitTest {
            given()
            val haircut = service("Haircut")
            val beard = service("Beard")
            val appointment = appointment(listOf(haircut, beard, haircut))

            whenn()
            val update = appointment.toUpdateRemote()

            then()
            assertEquals(
                listOf(RequestedServiceRemote(haircut.id, 2), RequestedServiceRemote(beard.id, 1)),
                update.services,
            )
        }

    @Test
    fun `update request carries the appointment id, assigned employee, note and date`() =
        runUnitTest {
            given()
            val appointment = appointment(listOf(service("Haircut"))).copy(note = "Window seat")

            whenn()
            val update = appointment.toUpdateRemote()

            then()
            assertEquals(appointment.id, update.id)
            assertEquals(appointment.employee.id, update.employeeId)
            assertEquals("Window seat", update.note)
            assertEquals(appointment.toRemote().date, update.date)
        }

    @Test
    fun `stored appointment keeps completer and price adjustment with its additional services`() =
        runUnitTest {
            given()
            val extra = service("Polish")
            val appointment = appointment(listOf(service("Haircut"))).copy(
                status = AppointmentStatus.COMPLETED,
                completedBy = AppointmentCompletedBy.USER,
                priceAdjustment = PriceAdjustment(
                    additionalServices = listOf(extra, extra),
                    price = Money(120.0, Money.SupportedCurrency.USD),
                    reason = "Extra polish",
                ),
            )

            whenn()
            val restored = appointment.toLocal().toDomain()

            then()
            assertEquals(appointment, restored)
        }

    @Test
    fun `stored appointment without adjustment restores no adjustment and no completer`() =
        runUnitTest {
            given()
            val appointment = appointment(listOf(service("Haircut")))

            whenn()
            val restored = appointment.toLocal().toDomain()

            then()
            assertNull(restored.priceAdjustment)
            assertNull(restored.completedBy)
        }

    @Test
    fun `stored no-show appointment restores its status`() = runUnitTest {
        given()
        val appointment = appointment(
            listOf(service("Haircut")),
        ).copy(status = AppointmentStatus.NO_SHOW)

        whenn()
        val restored = appointment.toLocal().toDomain()

        then()
        assertEquals(AppointmentStatus.NO_SHOW, restored.status)
    }

    @Test
    fun `decodes appended unresolved wire status into the domain status`() = runUnitTest {
        given()
        val payload = byteArrayOf(4)

        whenn()
        val status = dataSerializer.decodeFromByteArray(
            AppointmentStatusRemote.serializer(),
            payload,
        ).toDomain()

        then()
        assertEquals(AppointmentStatus.UNRESOLVED, status)
    }

    @Test
    fun `maps unresolved appointment to the appended remote status`() = runUnitTest {
        given()
        val appointment = appointment(listOf(service("Haircut"))).copy(
            status = AppointmentStatus.UNRESOLVED,
        )

        whenn()
        val remote = appointment.toRemote()

        then()
        assertEquals(AppointmentStatusRemote.UNRESOLVED, remote.status)
    }

    @Test
    fun `stored unresolved appointment restores its status`() = runUnitTest {
        given()
        val appointment = appointment(listOf(service("Haircut"))).copy(
            status = AppointmentStatus.UNRESOLVED,
        )

        whenn()
        val local = appointment.toLocal()
        val restored = local.toDomain()

        then()
        assertEquals("UNRESOLVED", local.entity.status)
        assertEquals(appointment, restored)
    }

    @Test
    fun `stored appointment keeps every booked count of a repeated service`() = runUnitTest {
        given()
        val haircut = service("Haircut")
        val beard = service("Beard")
        val appointment = appointment(listOf(haircut, beard, haircut))

        whenn()
        val restored = appointment.toLocal().toDomain()

        then()
        assertEquals(
            mapOf(haircut.id to 2, beard.id to 1),
            restored.services.groupingBy {
                it.id
            }.eachCount(),
        )
    }

    @Test
    fun `stored repeated service is written as one row with its count`() = runUnitTest {
        given()
        val haircut = service("Haircut")
        val appointment = appointment(listOf(haircut, haircut))

        whenn()
        val rows = appointment.toServiceEntities()

        then()
        assertEquals(listOf(haircut.id to 2), rows.map { it.id to it.count })
    }
}
