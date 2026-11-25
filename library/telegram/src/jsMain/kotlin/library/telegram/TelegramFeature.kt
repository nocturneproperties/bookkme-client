package library.telegram

enum class TelegramFeature(
    val minVersion: String,
) {
    BACK_BUTTON("6.1"),
    POPUP("6.2"),
    SETTINGS_BUTTON("7.0"),
    SECONDARY_BUTTON("7.10"),
    BOTTOM_BAR_COLOR("7.10"),
    DOWNLOAD_FILE("8.0"),
}

fun TelegramWebApp.supports(feature: TelegramFeature): Boolean {
    return isVersionAtLeast(feature.minVersion)
}
