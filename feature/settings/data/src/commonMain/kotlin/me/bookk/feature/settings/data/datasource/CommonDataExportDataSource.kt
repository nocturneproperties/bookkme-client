package me.bookk.feature.settings.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.accept
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import me.bookk.core.data.DataSource
import me.bookk.core.domain.entity.DataExport
import me.bookk.feature.settings.data.remote.api.UserRouting
import me.bookk.feature.settings.data.remote.model.DataExportRemote
import me.bookk.feature.settings.data.remote.model.DataExportRequestRemote
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import kotlin.uuid.Uuid

internal class CommonDataExportDataSource(private val httpClient: HttpClient) :
    DataSource(),
    DataExportDataSource {
    override suspend fun start(stepUpToken: String): DataExport = mapExceptions {
        httpClient.post(UserRouting.Api.User.Export()) {
            setBody(DataExportRequestRemote(stepUpToken))
        }.body<DataExportRemote>().toDomain()
    }

    override suspend fun get(): DataExport = mapExceptions {
        httpClient.get(UserRouting.Api.User.Export()).body<DataExportRemote>().toDomain()
    }

    override suspend fun download(): String = mapExceptions {
        httpClient.get(UserRouting.Api.User.Export.Download()) {
            accept(ContentType.Application.Json)
        }.bodyAsText()
    }
}
