package me.bookk.core.presentation.navigation

//Cant be an interface, because destinations should conform to Equatable and Hashable for IOs
abstract class NavigationDestination {
    abstract override fun equals(other: Any?): Boolean
    abstract override fun hashCode(): Int
}