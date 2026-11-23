package me.bookk.core.presentation.navigation

enum class BackStackDirection {
    FORWARD,
    BACKWARD,
}

class BackStackTransition<R : Any>(
    val from: BackStackEntry<R>,
    val to: BackStackEntry<R>,
    val direction: BackStackDirection,
) {
    val upperEntry: BackStackEntry<R>
        get() = if (direction == BackStackDirection.FORWARD) to else from

    val lowerEntry: BackStackEntry<R>
        get() = if (direction == BackStackDirection.FORWARD) from else to

    val entriesInStackOrder: List<BackStackEntry<R>>
        get() = listOf(lowerEntry, upperEntry)
}
