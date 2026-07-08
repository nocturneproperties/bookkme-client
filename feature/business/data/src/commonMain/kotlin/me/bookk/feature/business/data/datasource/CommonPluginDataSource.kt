package me.bookk.feature.business.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.feature.business.data.remote.api.AppointmentRouting.Api
import me.bookk.feature.business.data.remote.model.AppointmentsEnableRequest
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.datasource.PluginDataSource
import kotlin.uuid.Uuid

internal class CommonPluginDataSource(
    private val httpClient: HttpClient,
    preferenceProvider: PreferenceProvider,
) : DataSource(), PluginDataSource {

    private val preferences = preferenceProvider.get("plugin_prefs")

    override suspend fun enableAppointmentsPlugin(business: Business) {
        mapExceptions {
            httpClient.post(Api.Appointment.Enabled(businessId = business.id)) {
                setBody(
                    AppointmentsEnableRequest(
                        id = business.id,
                        name = business.name,
                        address = business.address,
                        timeZone = business.timeZone,
                        isEnabled = true
                    )
                )
            }
        }
    }

    override suspend fun isAppointmentPluginAvailableOnRemote(businessId: Uuid): Boolean =
        mapExceptions {
            httpClient.get(Api.Appointment.Enabled(businessId = businessId))
                .body()
        }

    override suspend fun saveAppointmentPluginAvailability(businessId: Uuid, isAvailable: Boolean) {
        preferences.set(Key.appointmentPluginAvailability(businessId), isAvailable)
    }

    override suspend fun getAppointmentPluginAvailability(businessId: Uuid): Boolean {
        return preferences.get(Key.appointmentPluginAvailability(businessId)) ?: false
    }

    private object Key {
        fun appointmentPluginAvailability(businessId: Uuid) =
            Preferences.Key<Boolean>("appointment_plugin_available_$businessId")
    }
}