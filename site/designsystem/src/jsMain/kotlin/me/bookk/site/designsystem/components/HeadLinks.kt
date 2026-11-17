package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.browser.document
import me.bookk.site.designsystem.resources.HeadLink
import org.w3c.dom.Element

private const val MANAGED_ATTRIBUTE = "data-head-link"

@Composable
fun HeadLinks(links: List<HeadLink>) {
    DisposableEffect(links) {
        removeManagedLinks()
        links.forEach { document.head?.appendChild(it.toElement()) }
        onDispose { removeManagedLinks() }
    }
}

private fun HeadLink.toElement(): Element {
    val element = document.createElement("link")
    element.setAttribute(MANAGED_ATTRIBUTE, "")
    element.setAttribute("rel", rel)
    element.setAttribute("href", href)
    hreflang?.let { element.setAttribute("hreflang", it) }
    return element
}

private fun removeManagedLinks() {
    val nodes = document.querySelectorAll("link[$MANAGED_ATTRIBUTE]")
    (0 until nodes.length)
        .mapNotNull { nodes.item(it) as? Element }
        .forEach { it.remove() }
}
