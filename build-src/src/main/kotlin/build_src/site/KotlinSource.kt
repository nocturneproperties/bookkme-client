package build_src.site

internal fun String.kotlinLiteral(): String {
    val escaped = replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("$", "\\$")
        .replace("\r", "")
        .replace("\t", "\\t")
        .replace("\n", "\\n")
    return "\"$escaped\""
}

internal fun List<String>.kotlinListOf(): String {
    return joinToString(prefix = "listOf(", postfix = ")") { it.kotlinLiteral() }
}
