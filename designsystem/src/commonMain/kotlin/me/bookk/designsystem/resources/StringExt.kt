package me.bookk.designsystem.resources

fun String.toOneLine(): String {
    return trimStart().replace("\n", "")
}

fun String.asPhone(): String {
    return toOneLine().filterIndexed { index, c ->
        c.isDigit() || (c == '+' && index == 0)
    }
}

