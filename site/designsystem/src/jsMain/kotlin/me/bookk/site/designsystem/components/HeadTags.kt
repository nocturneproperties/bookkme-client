package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import kotlinx.browser.document
import me.bookk.site.designsystem.resources.HeadLink
import me.bookk.site.designsystem.resources.HeadMeta
import org.w3c.dom.Element

private const val MANAGED_ATTRIBUTE = "data-head-tag"

@Composable
fun HeadTags(title: String, meta: List<HeadMeta>, links: List<HeadLink>) {
    DisposableEffect(title, meta, links) {
        document.title = title
        removeManagedTags()
        meta.forEach { document.head?.appendChild(it.toElement()) }
        links.forEach { document.head?.appendChild(it.toElement()) }
        onDispose { removeManagedTags() }
    }
}

private fun HeadMeta.toElement(): Element {
    val element = managedElement("meta")
    element.setAttribute(attribute.html, key)
    element.setAttribute("content", content)
    return element
}

private fun HeadLink.toElement(): Element {
    val element = managedElement("link")
    element.setAttribute("rel", rel)
    element.setAttribute("href", href)
    hreflang?.let { element.setAttribute("hreflang", it) }
    return element
}

private fun managedElement(tag: String): Element {
    val element = document.createElement(tag)
    element.setAttribute(MANAGED_ATTRIBUTE, "")
    return element
}

private fun removeManagedTags() {
    val nodes = document.querySelectorAll("[$MANAGED_ATTRIBUTE]")
    (0 until nodes.length)
        .mapNotNull { nodes.item(it) as? Element }
        .forEach { it.remove() }
}
