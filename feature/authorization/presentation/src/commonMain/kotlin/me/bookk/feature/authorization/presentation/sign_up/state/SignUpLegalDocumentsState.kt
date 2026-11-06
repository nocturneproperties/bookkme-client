package me.bookk.feature.authorization.presentation.sign_up.state

data class SignUpLegalDocumentsState(
    val privacyPolicy: SignUpLegalDocumentState,
    val termsOfService: SignUpLegalDocumentState,
)
