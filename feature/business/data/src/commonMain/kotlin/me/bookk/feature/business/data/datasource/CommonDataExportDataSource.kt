package me.bookk.feature.business.data.datasource

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
import me.bookk.feature.business.data.remote.api.BusinessRouting
import me.bookk.feature.business.data.remote.model.DataExportRemote
import me.bookk.feature.business.data.remote.model.DataExportRequestRemote
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import kotlin.uuid.Uuid

internal class CommonDataExportDataSource(private val httpClient: HttpClient) : DataSource(), DataExportDataSource {
    override suspend fun start(businessId: Uuid, stepUpToken: String): DataExport = mapExceptions {
        httpClient.post(BusinessRouting.Api.Business.Id.Export(BusinessRouting.Api.Business.Id(id = businessId))) {
            setBody(DataExportRequestRemote(stepUpToken))
        }.body<DataExportRemote>().toDomain()
    }

    override suspend fun get(businessId: Uuid): DataExport = mapExceptions {
        httpClient.get(BusinessRouting.Api.Business.Id.Export(BusinessRouting.Api.Business.Id(id = businessId))).body<DataExportRemote>().toDomain()
    }

    override suspend fun download(businessId: Uuid): String = mapExceptions {
        httpClient.get(BusinessRouting.Api.Business.Id.Export.Download(BusinessRouting.Api.Business.Id.Export(BusinessRouting.Api.Business.Id(id = businessId)))) {
            accept(ContentType.Application.Json)
        }.bodyAsText()
    }
}
