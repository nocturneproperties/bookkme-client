package build_src.constants

object ApplicationConfig {
    const val MIN_SDK = 28
    const val COMPILE_SDK = 36

    const val STATIC_VERSION = "0.1.0"
    const val STATIC_VERSION_CODE = 260571542

    val VERSION_NAME: String
        get() = System.getenv("BUILD_VERSION").orEmpty().ifBlank { STATIC_VERSION }
    val VERSION_CODE: Int
        get() = System.getenv("BUILD_VERSION_CODE").orEmpty().toIntOrNull() ?: STATIC_VERSION_CODE

    const val ROOT_PACKAGE = "me.bookk.android"
}