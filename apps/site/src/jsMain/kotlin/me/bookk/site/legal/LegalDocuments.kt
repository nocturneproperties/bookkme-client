package me.bookk.site.legal

object LegalDocuments {
    private val histories = LegalDocument.entries.associateWith {
        LegalDocumentHistory.from(it, publishedLegalTexts)
    }

    fun history(document: LegalDocument): LegalDocumentHistory {
        return histories.getValue(document)
    }
}
