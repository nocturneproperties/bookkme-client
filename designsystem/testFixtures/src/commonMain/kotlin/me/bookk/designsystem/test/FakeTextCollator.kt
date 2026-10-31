package me.bookk.designsystem.test

import me.bookk.core.presentation.text.TextCollator

class FakeTextCollator(
    private val comparator: Comparator<String> = compareBy { it.lowercase() }
) : TextCollator {

    override fun comparator(): Comparator<String> {
        return comparator
    }
}
