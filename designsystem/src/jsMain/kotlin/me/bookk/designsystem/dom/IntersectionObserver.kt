package me.bookk.designsystem.dom

import org.w3c.dom.Element

external class IntersectionObserver(
    callback: (entries: Array<IntersectionObserverEntry>, observer: IntersectionObserver) -> Unit,
    options: IntersectionObserverInit = definedExternally,
) {
    fun observe(target: Element)

    fun disconnect()
}

external interface IntersectionObserverEntry {
    val isIntersecting: Boolean
}

external interface IntersectionObserverInit {
    var rootMargin: String?
}
