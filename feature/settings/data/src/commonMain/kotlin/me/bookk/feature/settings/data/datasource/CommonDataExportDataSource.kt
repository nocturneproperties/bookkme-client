package me.bookk.feature.settings.data.datasource

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
import me.bookk.database.dao.PersonalDataExportDao
import me.bookk.feature.settings.data.mapping.toDomain
import me.bookk.feature.settings.data.mapping.toEntity
import me.bookk.feature.settings.data.remote.api.UserRouting
import me.bookk.feature.settings.data.remote.model.DataExportRemote
import me.bookk.feature.settings.data.remote.model.DataExportRequestRemote
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import okio.Path

internal class CommonDataExportDataSource(
    private val httpClient: HttpClient,
    private val personalDataExportDao: PersonalDataExportDao,
    private val fileProvider: FileProvider,
) : DataSource(),
    DataExportDataSource,
    LogOutAction {
    private val directory = fileProvider.touchCacheFile("personal_data_exports")

    override suspend fun getFile(export: DataExport): Path? = mapExceptions {
        export.file().takeIf { fileProvider.exists(it) }
    }

    override suspend fun saveFile(export: DataExport, content: String): Path = mapExceptions {
        fileProvider.deleteRecursively(directory)
        export.file().also { fileProvider.writeText(it, content) }
    }

    override suspend fun deleteFiles() = mapExceptions {
        fileProvider.deleteRecursively(directory)
    }

    private fun DataExport.file(): Path {
        val key = "${requestedAt?.toEpochMilliseconds()}-${expiresAt?.toEpochMilliseconds()}"
        return directory / key / FILE_NAME
    }

    override suspend fun start(confirmationToken: String): DataExport = mapExceptions {
        httpClient.post(UserRouting.Api.User.Export()) {
            setBody(DataExportRequestRemote(confirmationToken))
        }
            .body<DataExportRemote>()
            .toDomain()
    }

    override suspend fun get(): DataExport = mapExceptions {
        httpClient.get(UserRouting.Api.User.Export())
            .body<DataExportRemote>()
            .toDomain()
    }

    override suspend fun download(): String = mapExceptions {
        httpClient.get(UserRouting.Api.User.Export.Download()) {
            accept(ContentType.Application.Json)
        }.bodyAsText()
    }

    override fun observe(): Flow<DataExport?> {
        return personalDataExportDao.observe()
            .map { it?.toDomain() }
            .mapErrors()
    }

    override suspend fun saveInDb(export: DataExport) = mapExceptions {
        personalDataExportDao.upsert(export.toEntity())
    }

    override suspend fun doOnLogOut() {
        personalDataExportDao.clear()
        fileProvider.deleteRecursively(directory)
    }

    private companion object {
        const val FILE_NAME = "bookk-personal-data.json"
    }
}
