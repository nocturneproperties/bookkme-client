package me.bookk.feature.settings.domain.api.entity

enum class ColorScheme(val id: Long) {
    DARK(1),
    LIGHT(2),
    SYSTEM(3);

    companion object {
        fun from(id: Long?): ColorScheme {
            return id?.let { entries.first { it.id == id } } ?: SYSTEM
        }
    }
}