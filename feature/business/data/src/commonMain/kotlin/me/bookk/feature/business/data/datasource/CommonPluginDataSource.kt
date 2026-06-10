package me.bookk.feature.business.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.feature.business.data.remote.api.AppointmentRouting.Api
import me.bookk.feature.business.data.remote.model.AppointmentsEnableRequest
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.PluginDataSource
import kotlin.uuid.Uuid

internal class CommonPluginDataSource(
    private val httpClient: HttpClient,
) : DataSource(), PluginDataSource {

    override suspend fun enableAppointmentsPlugin(business: Business) {
        mapExceptions {
            httpClient.post(Api.Appointment.Enabled(businessId = business.id)) {
                setBody(
                    AppointmentsEnableRequest(
                        id = business.id,
                        name = business.name,
                        address = business.address,
                        isEnabled = true
                    )
                )
            }
        }
    }

    override suspend fun isAppointmentPluginAvailable(businessId: Uuid): Boolean = mapExceptions {
        httpClient.get(Api.Appointment.Enabled(businessId = businessId))
            .body()
    }
}