package library.notifications.api

data class LocalNotification(
    val id: String,
    val title: String,
    val body: String,
    val subtitle: String? = null,
    val group: String? = null,
    val badgeCount: Int? = null,
    val data: Map<String, String> = emptyMap()
)
