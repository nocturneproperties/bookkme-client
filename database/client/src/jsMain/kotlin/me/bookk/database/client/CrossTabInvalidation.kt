package me.bookk.database.client

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.bookk.database.ExternalChangeInvalidator
import org.w3c.dom.BroadcastChannel

const val DATABASE_CHANGES_CHANNEL = "bookkme-db-changes"

internal class CrossTabInvalidation(
    private val invalidator: ExternalChangeInvalidator,
    private val scope: CoroutineScope,
) {
    private val channel = BroadcastChannel(DATABASE_CHANGES_CHANNEL)

    fun start() {
        invalidate()
        channel.onmessage = { invalidate() }
    }

    private fun invalidate() {
        scope.launch { invalidator.invalidateIfChangedElsewhere() }
    }
}
