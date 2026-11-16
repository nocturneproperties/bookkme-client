package me.bookk.core.presentation.memory

external class WeakRef<T : Any>(
    target: T,
) {
    fun deref(): T?
}
