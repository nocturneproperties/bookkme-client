package me.bookk.core.text

import platform.Foundation.NSString
import platform.Foundation.create
import platform.Foundation.decomposedStringWithCanonicalMapping

internal actual fun String.canonicallyDecomposed(): String {
    return NSString.create(string = this).decomposedStringWithCanonicalMapping
}
