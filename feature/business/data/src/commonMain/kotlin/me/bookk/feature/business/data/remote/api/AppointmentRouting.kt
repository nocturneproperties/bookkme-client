package me.bookk.feature.business.data.remote.api

import io.ktor.resources.Resource
import kotlin.uuid.Uuid

object AppointmentRouting {
    @Resource("api")
    class Api {
        @Resource("/appointments")
        class Appointment(val parent: Api = Api()) {

            @Resource("/enabled/{businessId}")
            class Enabled(val parent: Appointment = Appointment(), val businessId: Uuid)
        }
    }
}