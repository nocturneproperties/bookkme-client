package me.bookk.feature.settings.presentation.export

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.feature.settings.domain.api.DownloadPersonalDataExport
import me.bookk.feature.settings.domain.api.GetPersonalDataExport
import me.bookk.feature.settings.domain.api.StartPersonalDataExport
import me.bookk.feature.settings.domain.api.entity.ActionOutcome
import okio.Path

internal class FakeExportCache {
    val snapshots = MutableSharedFlow<DataExport?>(replay = 1, extraBufferCapacity = 1).apply {
        tryEmit(null)
    }

    var value: DataExport?
        get() {
            return snapshots.replayCache.last()
        }
        set(value) {
            check(snapshots.tryEmit(value))
        }
}

internal class FakeGetPersonalDataExport(
    private val cache: FakeExportCache,
) : GetPersonalDataExport {
    var server: suspend () -> DataExport = { cache.value ?: DataExport(DataExportStatus.NONE) }
    var cachesServerResponse = true
    var observation: Flow<DataExport?>? = null
    var flowCalls = 0
        private set
    var refreshCalls = 0
        private set

    override fun flow(): Flow<DataExport?> {
        flowCalls++
        return observation ?: cache.snapshots
    }

    override suspend fun refresh(): DataExport {
        refreshCalls++
        val export = server()
        if (cachesServerResponse) cache.value = export
        return export
    }
}

internal class FakeStartPersonalDataExport(
    private val cache: FakeExportCache,
) : StartPersonalDataExport {
    var server: suspend () -> DataExport = { DataExport(DataExportStatus.RUNNING) }
    var chat: (suspend () -> ActionOutcome<DataExport>)? = null
    var cachesServerResponse = true
    var calls = 0
        private set

    override suspend fun invoke(): ActionOutcome<DataExport> {
        calls++
        return chat?.invoke() ?: ActionOutcome.Completed(startOnServer())
    }

    private suspend fun startOnServer(): DataExport {
        val export = server()
        if (cachesServerResponse) cache.value = export
        return export
    }
}

internal class FakeDownloadPersonalDataExport(
    var file: suspend () -> Path,
) : DownloadPersonalDataExport {
    var calls = 0
        private set

    override suspend fun invoke(): Path {
        calls++
        return file()
    }
}
