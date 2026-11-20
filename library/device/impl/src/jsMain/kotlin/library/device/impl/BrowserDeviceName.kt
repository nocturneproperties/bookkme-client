package library.device.impl

internal object BrowserDeviceName {
    private const val FALLBACK = "Web browser"

    private val browsers = listOf(
        "Edge" to listOf("Edg/", "EdgA/", "EdgiOS/"),
        "Opera" to listOf("OPR/", "OPT/"),
        "Samsung Internet" to listOf("SamsungBrowser/"),
        "Firefox" to listOf("Firefox/", "FxiOS/"),
        "Chrome" to listOf("Chrome/", "CriOS/"),
        "Safari" to listOf("Safari/"),
    )

    private val systems = listOf(
        "iOS" to listOf("iPhone", "iPad", "iPod"),
        "Android" to listOf("Android"),
        "ChromeOS" to listOf("CrOS"),
        "Windows" to listOf("Windows"),
        "macOS" to listOf("Macintosh", "Mac OS X"),
        "Linux" to listOf("Linux"),
    )

    fun from(userAgent: String): String {
        val browser = browsers.firstMatch(userAgent)
        val system = systems.firstMatch(userAgent)
        return when {
            browser != null && system != null -> "$browser on $system"
            else -> browser ?: system ?: FALLBACK
        }
    }

    private fun List<Pair<String, List<String>>>.firstMatch(userAgent: String): String? {
        return firstOrNull { (_, tokens) -> tokens.any { it in userAgent } }?.first
    }
}
