package me.bookk.core.presentation.text

import me.bookk.core.presentation.intl.Intl
import me.bookk.core.presentation.intl.intlLocales
import me.bookk.core.presentation.intl.jsOptions

internal class JsTextCollator : TextCollator {

    override fun comparator(): Comparator<String> {
        val collator = Intl.Collator(
            intlLocales(),
            jsOptions(
                "numeric" to true,
                "sensitivity" to "base",
            ),
        )
        return Comparator { first, second -> collator.compare(first, second) }
    }
}
