package me.bookk.feature.appointments.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.database.dao.AppointmentSettingsDao
import me.bookk.feature.appointments.data.mapping.toDayOffEntities
import me.bookk.feature.appointments.data.mapping.toDayScheduleEntities
import me.bookk.feature.appointments.data.mapping.toDomain
import me.bookk.feature.appointments.data.mapping.toEntity
import me.bookk.feature.appointments.data.mapping.toRemote
import me.bookk.feature.appointments.data.mapping.toWorkHourEntities
import me.bookk.feature.appointments.data.remote.api.AppointmentRouting.Api.Appointment.Settings
import me.bookk.feature.appointments.data.remote.model.AppointmentSettingsRemote
import me.bookk.feature.appointments.domain.api.entity.AppointmentSettings
import me.bookk.feature.appointments.domain.datasource.AppointmentSettingsDataSource
import kotlin.uuid.Uuid

internal class CommonAppointmentSettingsDataSource(
    private val httpClient: HttpClient,
    private val appointmentSettingsDao: AppointmentSettingsDao
) : DataSource(), AppointmentSettingsDataSource {

    override suspend fun getAppointmentSettings(businessId: Uuid): AppointmentSettings =
        mapExceptions {
            httpClient.get(Settings(businessId = businessId))
                .body<AppointmentSettingsRemote>()
                .toDomain()
        }

    override suspend fun updateAppointmentSettings(settings: AppointmentSettings): AppointmentSettings =
        mapExceptions {
            httpClient.put(Settings(businessId = settings.businessId)) {
                setBody(settings.toRemote())
            }
                .body<AppointmentSettingsRemote>()
                .toDomain()
        }

    override suspend fun getAppointmentSettingsFromDB(businessId: Uuid): AppointmentSettings? =
        mapExceptions {
            appointmentSettingsDao.getByBusinessId(businessId)?.toDomain()
        }

    override suspend fun saveAppointmentSettingsInDB(settings: AppointmentSettings) {
        mapExceptions {
            appointmentSettingsDao.upsertWithChildren(
                settings = settings.toEntity(),
                daySchedules = settings.toDayScheduleEntities(),
                workHours = settings.toWorkHourEntities(),
                dayOffs = settings.toDayOffEntities()
            )
        }
    }
}
