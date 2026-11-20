package org.w3c.dom.events

fun EventListener(handler: (Event) -> Unit): EventListener {
    return FunctionEventListener(handler)
}

private class FunctionEventListener(
    private val handler: (Event) -> Unit,
) : EventListener {
    override fun handleEvent(event: Event) {
        handler(event)
    }
}
