package me.bookk.core.domain.launch

enum class ConfirmableAction(
    val kind: String,
) {
    DELETE_ACCOUNT("delete-account"),
    EXPORT_PERSONAL_DATA("export-personal"),
}
