package me.bookk.feature.settings.domain.api

import me.bookk.feature.settings.domain.api.entity.AcceptedLegalDocuments

interface AcceptLegalDocuments {
    suspend operator fun invoke(documents: AcceptedLegalDocuments)

    sealed interface Error {
        class UnknownVersion : Exception(), Error
    }
}
