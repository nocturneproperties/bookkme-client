package library.telegram

enum class TelegramFeature(
    val minVersion: String,
) {
    BACK_BUTTON("6.1"),
    POPUP("6.2"),
    SECONDARY_BUTTON("7.10"),
    BOTTOM_BAR_COLOR("7.10"),
}

fun TelegramWebApp.supports(feature: TelegramFeature): Boolean {
    return isVersionAtLeast(feature.minVersion)
}
