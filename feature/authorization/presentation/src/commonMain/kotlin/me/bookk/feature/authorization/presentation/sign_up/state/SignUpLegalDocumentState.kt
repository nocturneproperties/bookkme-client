package me.bookk.feature.authorization.presentation.sign_up.state

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.feature.authorization.domain.entity.SignUpLegalDocument

interface SignUpLegalDocumentState {
    var document: SignUpLegalDocument?
    var linkText: StringDesc
    var linkUrl: String
    var onLinkClick: (() -> Unit)?
}
