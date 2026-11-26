package me.bookk.feature.settings.presentation.confirmation

import dev.icerock.moko.resources.desc.StringDesc

interface ChatConfirmationState {
    var notice: StringDesc?
    var isWriteAccessRequested: Boolean
    var onWriteAccessAnswered: (Boolean) -> Unit
}
