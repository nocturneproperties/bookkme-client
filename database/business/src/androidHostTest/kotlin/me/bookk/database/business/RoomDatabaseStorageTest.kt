package me.bookk.database.business

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.RoomDatabaseStorage
import me.bookk.database.business.migration.prepareSqliteTestDriver
import java.io.File
import java.nio.file.Files
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RoomDatabaseStorageTest {
    @BeforeTest
    fun setUp() {
        prepareSqliteTestDriver()
    }

    @Test
    fun `reports an in-memory database as not persistent`() = runUnitTest {
        given()
        val database = Room.inMemoryDatabaseBuilder<BusinessDatabase>()
            .setDriver(BundledSQLiteDriver())
            .build()
        val sut = RoomDatabaseStorage(database)

        whenn()
        val result = database.closeAfter { sut.isPersistent() }

        then()
        assertFalse(result)
    }

    @Test
    fun `reports a file database as persistent`() = runUnitTest {
        given()
        val directory = Files.createTempDirectory("bookk-storage-test").toFile()
        directory.deleteOnExit()
        val database = Room.databaseBuilder<BusinessDatabase>(
            name = File(directory, "storage.db").absolutePath,
        )
            .setDriver(BundledSQLiteDriver())
            .build()
        val sut = RoomDatabaseStorage(database)

        whenn()
        val result = database.closeAfter { sut.isPersistent() }

        then()
        assertTrue(result)
        directory.deleteRecursively()
    }

    private suspend fun <T> BusinessDatabase.closeAfter(action: suspend () -> T): T {
        return try {
            action()
        } finally {
            close()
        }
    }
}
