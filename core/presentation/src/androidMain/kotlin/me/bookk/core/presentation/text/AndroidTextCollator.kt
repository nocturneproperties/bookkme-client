package me.bookk.core.presentation.text

import java.text.Collator
import java.util.Locale

internal class AndroidTextCollator : TextCollator {

    override fun comparator(): Comparator<String> {
        val collator = Collator.getInstance(Locale.getDefault()).apply { strength = Collator.SECONDARY }
        return Comparator { first, second -> collator.compare(first, second) }
    }
}
