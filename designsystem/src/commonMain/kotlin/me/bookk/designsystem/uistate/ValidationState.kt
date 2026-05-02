package me.bookk.designsystem.uistate

enum class ValidationState {
    ERROR,
    WARNING,
    DEFAULT;

    fun isAtLeastWarning() = this != DEFAULT
}