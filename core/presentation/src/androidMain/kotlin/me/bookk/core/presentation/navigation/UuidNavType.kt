package me.bookk.core.presentation.navigation

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import kotlin.uuid.Uuid

object UuidNavType : NavType<Uuid>(isNullableAllowed = false) {
    override fun put(bundle: SavedState, key: String, value: Uuid) {
        bundle.putString(key, value.toString())
    }

    override fun get(bundle: SavedState, key: String): Uuid? {
        return bundle.getString(key)?.let { Uuid.parse(it) }
    }

    override fun parseValue(value: String): Uuid {
        return Uuid.parse(value)
    }
}