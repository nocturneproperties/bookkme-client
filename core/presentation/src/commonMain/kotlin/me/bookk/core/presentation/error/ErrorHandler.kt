package me.bookk.core.presentation.error

interface ErrorHandler {
    fun handle(e: PresentationError)
}