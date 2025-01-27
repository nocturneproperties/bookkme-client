package me.bookk.core.presentation.navigation

interface NavigationDestination {
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}