package me.bookk.designsystem.uistate

import me.bookk.core.presentation.error.PresentationError

interface ErrorState {
    //Immutable List is better here because of ObjC/Kotlin interoperability
    val presentationError: List<PresentationError>

    fun add(error: PresentationError)
    fun removeFirst()
}