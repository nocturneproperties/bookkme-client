package me.bookk.feature.settings.presentation.dashboard

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.ButtonState

interface LegalDocumentsSection {
    var title: StringDesc
    var documents: List<LegalDocumentItem>
    var isLoading: Boolean
    var message: StringDesc?
    val accept: ButtonState
    val retry: ButtonState
}

data class LegalDocumentItem(
    val id: String,
    val title: StringDesc,
    val version: String,
    val status: StringDesc,
    val statusColor: ColorToken,
    val onClick: () -> Unit,
)
