package me.bookk.feature.settings.domain.api.entity

sealed interface ActionOutcome<out T> {
    data class Completed<T>(
        val value: T,
    ) : ActionOutcome<T>

    data object ConfirmationSent : ActionOutcome<Nothing>
}
