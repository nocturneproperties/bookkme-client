package me.bookk.core.presentation.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.json.Json
import kotlin.reflect.KType
import kotlin.reflect.typeOf

inline fun <reified T : Any> serializableNavTypeEntry(isNullableAllowed: Boolean = false): Pair<KType, NavType<T>> {
    return typeOf<T>() to navTypeOf<T>(isNullableAllowed)
}


inline fun <reified T> navTypeOf(
    isNullableAllowed: Boolean = false,
    json: Json = Json,
) = object : NavType<T>(isNullableAllowed) {
    override fun get(bundle: Bundle, key: String): T? {
        return bundle.getString(key)?.let { json.decodeFromString<T>(it) }
    }

    override fun parseValue(value: String): T {
        return json.decodeFromString<T>(Uri.decode(value))
    }

    override fun serializeAsValue(value: T): String {
        return Uri.encode(json.encodeToString(value))
    }

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, json.encodeToString(value))
    }
}