package me.bookk.feature.business.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.accept
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import library.files.api.FileProvider
import me.bookk.core.data.DataSource
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.BusinessDataExportDao
import me.bookk.feature.business.data.mapping.toDomain
import me.bookk.feature.business.data.mapping.toEntity
import me.bookk.feature.business.data.remote.api.BusinessRouting
import me.bookk.feature.business.data.remote.model.DataExportRemote
import me.bookk.feature.business.data.remote.model.DataExportRequestRemote
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import okio.Path
import kotlin.uuid.Uuid

internal class CommonDataExportDataSource(
    private val httpClient: HttpClient,
    private val businessDataExportDao: BusinessDataExportDao,
    private val fileProvider: FileProvider,
) : DataSource(),
    DataExportDataSource,
    LogOutAction {

    private val directory = fileProvider.touchCacheFile("business_data_exports")

    override suspend fun start(businessId: Uuid, confirmationToken: String): DataExport =
        mapExceptions {
            httpClient.post(exportRoute(businessId)) {
                setBody(DataExportRequestRemote(confirmationToken))
            }
                .body<DataExportRemote>()
                .toDomain()
        }

    override suspend fun get(businessId: Uuid): DataExport = mapExceptions {
        httpClient.get(exportRoute(businessId))
            .body<DataExportRemote>()
            .toDomain()
    }

    override suspend fun download(businessId: Uuid): String = mapExceptions {
        httpClient.get(BusinessRouting.Api.Business.Id.Export.Download(exportRoute(businessId))) {
            accept(ContentType.Application.Json)
        }.bodyAsText()
    }

    private fun exportRoute(businessId: Uuid): BusinessRouting.Api.Business.Id.Export {
        return BusinessRouting.Api.Business.Id.Export(
            BusinessRouting.Api.Business.Id(id = businessId),
        )
    }

    override fun observe(businessId: Uuid): Flow<DataExport?> {
        return businessDataExportDao.observe(businessId)
            .map { it?.toDomain() }
            .mapErrors()
    }

    override suspend fun getAllFromDb(): Map<Uuid, DataExport> = mapExceptions {
        businessDataExportDao.getAll().associate { it.businessId to it.toDomain() }
    }

    override suspend fun saveInDb(businessId: Uuid, export: DataExport) = mapExceptions {
        businessDataExportDao.upsert(export.toEntity(businessId))
    }

    override suspend fun deleteFromDb(businessId: Uuid) = mapExceptions {
        businessDataExportDao.delete(businessId)
    }

    override suspend fun getFile(businessId: Uuid, export: DataExport): Path? = mapExceptions {
        file(businessId, export).takeIf { fileProvider.exists(it) }
    }

    override suspend fun saveFile(businessId: Uuid, export: DataExport, content: String): Path =
        mapExceptions {
            fileProvider.deleteRecursively(businessDirectory(businessId))
            file(businessId, export).also { fileProvider.writeText(it, content) }
        }

    override suspend fun deleteFiles(businessId: Uuid) = mapExceptions {
        fileProvider.deleteRecursively(businessDirectory(businessId))
    }

    private fun businessDirectory(businessId: Uuid): Path {
        return directory / businessId.toString()
    }

    private fun file(businessId: Uuid, export: DataExport): Path {
        val key = "${export.requestedAt?.toEpochMilliseconds()}-" +
            "${export.expiresAt?.toEpochMilliseconds()}"
        return businessDirectory(businessId) / key / FILE_NAME
    }

    override suspend fun doOnLogOut() {
        businessDataExportDao.clear()
        fileProvider.deleteRecursively(directory)
    }

    private companion object {
        const val FILE_NAME = "bookk-business-data.json"
    }
}
