package me.bookk.feature.business.presentation.screen.export

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.feature.business.domain.api.DownloadBusinessDataExport
import me.bookk.feature.business.domain.api.GetBusinessDataExport
import me.bookk.feature.business.domain.api.StartBusinessDataExport
import okio.Path
import kotlin.uuid.Uuid

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

internal class FakeGetBusinessDataExport(
    private val cache: FakeExportCache,
) : GetBusinessDataExport {
    var server: suspend () -> DataExport = { cache.value ?: DataExport(DataExportStatus.NONE) }
    var cachesServerResponse = true
    var observation: Flow<DataExport?>? = null
    val businessIds = mutableListOf<Uuid>()
    var flowCalls = 0
        private set
    var refreshCalls = 0
        private set

    override fun flow(businessId: Uuid): Flow<DataExport?> {
        flowCalls++
        businessIds += businessId
        return observation ?: cache.snapshots
    }

    override suspend fun refresh(businessId: Uuid): DataExport {
        refreshCalls++
        businessIds += businessId
        val export = server()
        if (cachesServerResponse) cache.value = export
        return export
    }
}

internal class FakeStartBusinessDataExport(
    private val cache: FakeExportCache,
) : StartBusinessDataExport {
    var server: suspend () -> DataExport = { DataExport(DataExportStatus.RUNNING) }
    var cachesServerResponse = true
    val businessIds = mutableListOf<Uuid>()
    var calls = 0
        private set

    override suspend fun invoke(businessId: Uuid): DataExport {
        calls++
        businessIds += businessId
        val export = server()
        if (cachesServerResponse) cache.value = export
        return export
    }
}

internal class FakeDownloadBusinessDataExport(
    var file: suspend () -> Path,
) : DownloadBusinessDataExport {
    val businessIds = mutableListOf<Uuid>()
    var calls = 0
        private set

    override suspend fun invoke(businessId: Uuid): Path {
        calls++
        businessIds += businessId
        return file()
    }
}
