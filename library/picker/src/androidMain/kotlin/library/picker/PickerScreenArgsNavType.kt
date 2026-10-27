package library.picker

import android.os.Bundle
import androidx.navigation.NavType
import kotlinx.serialization.json.Json

internal val PickerScreenArgsNavType = object : NavType<PickerScreenArgs>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): PickerScreenArgs? {
        return bundle.getString(key)?.let { Json.decodeFromString(it) }
    }

    override fun parseValue(value: String): PickerScreenArgs {
        return Json.decodeFromString(value)
    }

    override fun serializeAsValue(value: PickerScreenArgs): String {
        return Json.encodeToString(value)
    }

    override fun put(bundle: Bundle, key: String, value: PickerScreenArgs) {
        bundle.putString(key, Json.encodeToString(value))
    }
}