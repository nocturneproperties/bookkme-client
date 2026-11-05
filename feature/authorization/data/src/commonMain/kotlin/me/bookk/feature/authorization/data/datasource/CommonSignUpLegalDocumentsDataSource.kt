package me.bookk.feature.authorization.data.datasource

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set
import me.bookk.feature.authorization.data.local.SignUpLegalDocumentsLocal
import me.bookk.feature.authorization.domain.datasource.SignUpLegalDocumentsDataSource
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocuments

internal class CommonSignUpLegalDocumentsDataSource(preferenceProvider: PreferenceProvider) :
    SignUpLegalDocumentsDataSource {
    private val preferences = preferenceProvider.get("signup_legal_documents_prefs")

    override suspend fun getDocuments(): SignUpLegalDocuments? {
        return preferences.get(Key.documents)?.let {
            Json.decodeFromString<SignUpLegalDocumentsLocal>(it).toDomain()
        }
    }

    override suspend fun saveDocuments(documents: SignUpLegalDocuments) {
        val local = SignUpLegalDocumentsLocal(
            privacyPolicyVersion = documents.privacyPolicy.version,
            privacyPolicyUrl = documents.privacyPolicy.url,
            termsOfServiceVersion = documents.termsOfService.version,
            termsOfServiceUrl = documents.termsOfService.url,
        )
        preferences.set(Key.documents, Json.encodeToString(local))
    }

    private object Key {
        val documents = Preferences.Key<String>("documents")
    }
}
