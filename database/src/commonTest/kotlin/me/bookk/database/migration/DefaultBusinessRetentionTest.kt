package me.bookk.database.migration

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteException
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.entity.BusinessEntity
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

class DefaultBusinessRetentionTest {
    @BeforeTest
    fun setUp() {
        prepareSqliteTestDriver()
    }

    private class Fixture {
        val connection = BundledSQLiteDriver().open(":memory:").apply {
            execSQL("PRAGMA foreign_keys = ON")
            execSQL(
                "CREATE TABLE business (id TEXT NOT NULL PRIMARY KEY, " +
                    "name TEXT NOT NULL, clientDataRetentionMonths INTEGER)",
            )
            execSQL(
                "CREATE TABLE child (businessId TEXT NOT NULL " +
                    "REFERENCES business(id) ON DELETE CASCADE)",
            )
            execSQL("INSERT INTO business VALUES ('unknown', 'Old business', NULL)")
            execSQL("INSERT INTO business VALUES ('custom', 'Custom business', 60)")
            execSQL("INSERT INTO child VALUES ('unknown')")
            execSQL("INSERT INTO child VALUES ('custom')")
        }
        val sut = DefaultBusinessRetention
    }

    @Test
    fun `defaults a new business entity to 36 months`() = runUnitTest {
        given()

        whenn()
        val entity = BusinessEntity(
            id = Uuid.NIL,
            name = "Business",
            description = "",
            address = "",
            locationLat = null,
            locationLng = null,
            currencyCode = "USD",
            phone = null,
            insta = null,
            viber = null,
            whatsApp = null,
            telegram = null,
            timeZone = "UTC",
        )

        then()
        assertEquals(36, entity.clientDataRetentionMonths)
    }

    @Test
    fun `backfills missing retention with 36 months`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.connection.use { connection ->
            whenn()
            fixture.sut.migrate(connection)

            then()
            assertEquals(36, connection.retention("unknown"))
        }
    }

    @Test
    fun `preserves custom retention during migration`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.connection.use { connection ->
            whenn()
            fixture.sut.migrate(connection)

            then()
            assertEquals(60, connection.retention("custom"))
        }
    }

    @Test
    fun `preserves business details and dependent rows during migration`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.connection.use { connection ->
            whenn()
            fixture.sut.migrate(connection)

            then()
            connection.prepare("SELECT name FROM business ORDER BY id").use {
                assertTrue(it.step())
                assertEquals("Custom business", it.getText(0))
                assertTrue(it.step())
                assertEquals("Old business", it.getText(0))
                assertFalse(it.step())
            }
            connection.prepare("SELECT COUNT(*) FROM child").use {
                assertTrue(it.step())
                assertEquals(2, it.getInt(0))
            }
            connection.prepare("PRAGMA foreign_key_check").use {
                assertFalse(it.step())
            }
        }
    }

    @Test
    fun `defaults omitted retention in future database inserts`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.connection.use { connection ->
            fixture.sut.migrate(connection)

            whenn()
            connection.execSQL("INSERT INTO business (id, name) VALUES ('new', 'New business')")

            then()
            assertEquals(36, connection.retention("new"))
        }
    }

    @Test
    fun `rejects null retention after migration`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.connection.use { connection ->
            fixture.sut.migrate(connection)

            whenn()
            assertFailsWith<SQLiteException> {
                connection.execSQL(
                    "INSERT INTO business (id, name, clientDataRetentionMonths) " +
                        "VALUES ('new', 'New business', NULL)",
                )
            }

            then()
            connection.prepare(
                "SELECT \"notnull\" FROM pragma_table_info('business') WHERE name = ?",
            )
                .use {
                    it.bindText(1, "clientDataRetentionMonths")
                    assertTrue(it.step())
                    assertEquals(1, it.getInt(0))
                }
        }
    }

    private fun SQLiteConnection.retention(id: String): Int {
        return prepare("SELECT clientDataRetentionMonths FROM business WHERE id = ?").use {
            it.bindText(1, id)
            check(it.step())
            it.getInt(0)
        }
    }
}

internal expect fun prepareSqliteTestDriver()
