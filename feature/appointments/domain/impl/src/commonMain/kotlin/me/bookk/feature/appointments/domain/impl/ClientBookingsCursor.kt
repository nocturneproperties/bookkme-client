package me.bookk.feature.appointments.domain.impl

import kotlin.time.Instant

internal sealed interface ListPosition {
    data object Start : ListPosition

    data class At(
        val cursor: String,
    ) : ListPosition

    data object End : ListPosition
}

internal data class ClientBookingsCursor(
    val appointments: ListPosition,
    val requests: ListPosition,
    val coveredUntil: Instant?,
) {
    val isAtEnd: Boolean
        get() = appointments == ListPosition.End && requests == ListPosition.End

    fun encode(): String {
        val covered = coveredUntil?.toEpochMilliseconds()?.toString() ?: NOTHING_COVERED
        return listOf(appointments.encode(), requests.encode(), covered).joinToString(SEPARATOR)
    }

    companion object {
        val START = ClientBookingsCursor(ListPosition.Start, ListPosition.Start, null)

        private const val SEPARATOR = " "
        private const val NOTHING_COVERED = "-"
        private const val START_TOKEN = "start"
        private const val END_TOKEN = "end"
        private const val CURSOR_PREFIX = "at:"

        fun decode(value: String): ClientBookingsCursor? {
            val parts = value.split(SEPARATOR)
            if (parts.size != 3) return null
            val appointments = decodePosition(parts[0]) ?: return null
            val requests = decodePosition(parts[1]) ?: return null
            val covered = parts[2]
            if (covered == NOTHING_COVERED) {
                return ClientBookingsCursor(appointments, requests, null)
            }
            val millis = covered.toLongOrNull() ?: return null
            return ClientBookingsCursor(
                appointments,
                requests,
                Instant.fromEpochMilliseconds(millis),
            )
        }

        private fun decodePosition(token: String): ListPosition? {
            return when {
                token == START_TOKEN -> {
                    ListPosition.Start
                }

                token == END_TOKEN -> {
                    ListPosition.End
                }

                token.startsWith(CURSOR_PREFIX) -> {
                    ListPosition.At(token.removePrefix(CURSOR_PREFIX))
                }

                else -> {
                    null
                }
            }
        }

        private fun ListPosition.encode(): String {
            return when (this) {
                ListPosition.Start -> START_TOKEN
                ListPosition.End -> END_TOKEN
                is ListPosition.At -> CURSOR_PREFIX + cursor
            }
        }
    }
}
