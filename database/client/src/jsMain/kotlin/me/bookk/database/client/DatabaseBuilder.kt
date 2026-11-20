package me.bookk.database.client

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import org.w3c.dom.MODULE
import org.w3c.dom.Worker
import org.w3c.dom.WorkerOptions
import org.w3c.dom.WorkerType

const val SQLITE_WORKER_PATH = "/sqlite/sqlite-worker.js"

fun getDatabaseBuilder(): RoomDatabase.Builder<ClientDatabase> {
    return Room.databaseBuilder<ClientDatabase>(name = ClientDatabase.DATABASE_NAME)
        .setDriver(WebWorkerSQLiteDriver(createSqliteWorker()))
}

private fun createSqliteWorker(): Worker {
    return Worker(SQLITE_WORKER_PATH, WorkerOptions(type = WorkerType.MODULE))
}
