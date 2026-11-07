package me.bookk.feature.settings.presentation.privacy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidButtonState

internal class AndroidLegalDocumentsSectionState : LegalDocumentsSection {
    override var title: StringDesc by mutableStateOf("".desc())
    override var documents: List<LegalDocumentItem> by mutableStateOf(emptyList())
    override var isLoading: Boolean by mutableStateOf(false)
    override var message: StringDesc? by mutableStateOf(null)
    override val accept = AndroidButtonState()
    override val retry = AndroidButtonState()
}
