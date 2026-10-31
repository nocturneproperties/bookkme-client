package me.bookk.core.presentation.text

import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.localizedStandardCompare

internal class IosTextCollator : TextCollator {

    @OptIn(BetaInteropApi::class)
    override fun comparator(): Comparator<String> {
        return Comparator { first, second -> NSString.create(string = first).localizedStandardCompare(second).toInt() }
    }
}
