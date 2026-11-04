package me.bookk.feature.settings.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.feature.settings.data.remote.api.UserRouting
import me.bookk.feature.settings.data.remote.model.AcceptedLegalDocumentsRemote
import me.bookk.feature.settings.data.remote.model.LegalDocumentStatusRemote
import me.bookk.feature.settings.data.remote.model.LegalDocumentVersionRemote
import me.bookk.feature.settings.domain.api.entity.AcceptedLegalDocuments
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.domain.api.entity.LegalDocumentVersion
import me.bookk.feature.settings.domain.datasource.LegalDocumentsDataSource

internal class CommonLegalDocumentsDataSource(private val httpClient: HttpClient) :
    DataSource(),
    LegalDocumentsDataSource {
    override suspend fun accept(documents: AcceptedLegalDocuments) = mapExceptions {
        httpClient.post(UserRouting.Api.User.Me.Legal()) {
            setBody(AcceptedLegalDocumentsRemote(documents.privacyPolicy, documents.termsOfService))
        }
        Unit
    }

    override suspend fun getVersions(): List<LegalDocumentVersion> = mapExceptions {
        httpClient.get(UserRouting.Api.User.Legal())
            .body<List<LegalDocumentVersionRemote>>()
            .map { it.toDomain() }
    }

    override suspend fun getStatuses(): List<LegalDocumentStatus> = mapExceptions {
        httpClient.get(UserRouting.Api.User.Me.Legal())
            .body<List<LegalDocumentStatusRemote>>()
            .map { it.toDomain() }
    }
}
