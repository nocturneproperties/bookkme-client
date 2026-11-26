package me.bookk.core.domain.launch

import kotlin.uuid.Uuid

sealed interface LaunchParameter {
    data object None : LaunchParameter

    data class Business(
        val id: Uuid,
    ) : LaunchParameter

    data class BusinessLink(
        val name: String,
    ) : LaunchParameter

    data class Confirmation(
        val action: ConfirmableAction,
    ) : LaunchParameter

    data object UnsupportedOperation : LaunchParameter

    companion object {
        private const val OPERATION_PREFIX = "op_"
        private const val CHALLENGE_PATTERN = "[0-9a-f]{32}"

        fun parse(value: String): LaunchParameter {
            val businessId = Uuid.parseHexDashOrNull(value)
            return when {
                value.startsWith(OPERATION_PREFIX) -> parseOperation(value)
                businessId != null -> Business(businessId)
                value.isNotBlank() -> BusinessLink(value)
                else -> None
            }
        }

        private fun parseOperation(value: String): LaunchParameter {
            val action = ConfirmableAction.entries.firstOrNull { it.launchPattern().matches(value) }
            return action?.let(::Confirmation) ?: UnsupportedOperation
        }

        private fun ConfirmableAction.launchPattern(): Regex {
            return Regex(Regex.escape("$OPERATION_PREFIX${kind}_") + CHALLENGE_PATTERN)
        }
    }
}
