package me.bookk.feature.authorization.domain.datasource

import me.bookk.feature.authorization.domain.entity.SignUpLegalDocuments

interface SignUpLegalDocumentsDataSource {
    suspend fun getDocuments(): SignUpLegalDocuments?
    suspend fun saveDocuments(documents: SignUpLegalDocuments)
}
