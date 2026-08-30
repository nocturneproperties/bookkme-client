package me.bookk.feature.business.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.business.data.remote.api.AppointmentRouting.Api
import me.bookk.feature.business.domain.datasource.PluginDataSource
import kotlin.uuid.Uuid

internal class CommonPluginDataSource(
    private val httpClient: HttpClient,
    preferenceProvider: PreferenceProvider,
) : DataSource(), PluginDataSource, LogOutAction {

    private val preferences = preferenceProvider.get("plugin_prefs")

    override suspend fun enableAppointmentsPlugin(businessId: Uuid) {
        mapExceptions {
            httpClient.post(Api.Appointment.Enabled(businessId = businessId))
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

    override suspend fun getAppointmentPluginAvailability(businessId: Uuid): Boolean? {
        return preferences.get(Key.appointmentPluginAvailability(businessId))
    }

    override suspend fun doOnLogOut() {
        preferences.clear()
    }

    private object Key {
        fun appointmentPluginAvailability(businessId: Uuid) =
            Preferences.Key<Boolean>("appointment_plugin_available_$businessId")
    }
}