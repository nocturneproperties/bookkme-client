package me.bookk.feature.appointments.data.remote.api

import io.ktor.resources.Resource
import kotlinx.datetime.LocalDate
import kotlin.uuid.Uuid

object AppointmentRouting {
    @Resource("api")
    class Api {
        @Resource("/appointments")
        class Appointment(val parent: Api = Api()) {

            @Resource("/instant")
            class Instant(val parent: Appointment = Appointment())

            @Resource("/settings/{businessId}")
            class Settings(val parent: Appointment = Appointment(), val businessId: Uuid)

            @Resource("/list/{businessId}")
            class List(val parent: Api = Api(), val businessId: Uuid, val date: LocalDate)

            @Resource("/history/{businessId}")
            class History(val parent: Api = Api(), val businessId: Uuid, val limit: Int, val offset: Long)

            @Resource("/request/{businessId}")
            class Requests(
                val parent: Appointment = Appointment(),
                val businessId: Uuid,
            )

            @Resource("/request")
            class Request(val parent: Appointment = Appointment())

            @Resource("/{id}/cancel")
            class Cancel(val parent: Appointment = Appointment(), val id: Uuid)

            @Resource("/{id}")
            class Id(val parent: Appointment = Appointment(), val id: Uuid)
        }
    }
}
