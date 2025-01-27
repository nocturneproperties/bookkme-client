package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import me.bookk.core.presentation.error.PresentationError

@Immutable
class AndroidErrorState : ErrorState {
    override val presentationError: MutableList<PresentationError> = mutableStateListOf()

    override fun add(error: PresentationError) {
        presentationError.add(error)
    }

    override fun removeFirst() {
        presentationError.removeAt(0)
    }
}