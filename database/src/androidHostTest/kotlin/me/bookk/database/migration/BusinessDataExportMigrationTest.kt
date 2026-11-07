package me.bookk.database.migration

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.AppDatabase_AutoMigration_25_26_Impl
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BusinessDataExportMigrationTest {
    @BeforeTest
    fun setUp() {
        prepareSqliteTestDriver()
    }

    private class Fixture {
        val connection = BundledSQLiteDriver().open(":memory:").apply {
            execSQL("CREATE TABLE personal_data_export (id INTEGER NOT NULL PRIMARY KEY)")
            execSQL("INSERT INTO personal_data_export VALUES (0)")
        }
        val sut = AppDatabase_AutoMigration_25_26_Impl()
    }

    @Test
    fun `adds an empty business export cache while preserving existing data`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.connection.use { connection ->
            whenn()
            fixture.sut.migrate(connection)

            then()
            connection.prepare("SELECT id FROM personal_data_export").use {
                assertTrue(it.step())
                assertEquals(0L, it.getLong(0))
                assertFalse(it.step())
            }
            connection.prepare("SELECT * FROM business_data_export").use {
                assertFalse(it.step())
            }
        }
    }

    @Test
    fun `stores one status per business with nullable timestamps`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.connection.use { connection ->
            fixture.sut.migrate(connection)

            whenn()
            connection.execSQL(
                "INSERT INTO business_data_export " +
                    "(businessId, status, requestedAt, expiresAt, availableAgainAt) " +
                    "VALUES ('first', 'NONE', NULL, NULL, NULL), ('second', 'READY', 1, 2, 3)",
            )

            then()
            connection.prepare(
                "SELECT status, requestedAt FROM business_data_export ORDER BY businessId",
            ).use {
                assertTrue(it.step())
                assertEquals("NONE", it.getText(0))
                assertTrue(it.isNull(1))
                assertTrue(it.step())
                assertEquals("READY", it.getText(0))
                assertEquals(1L, it.getLong(1))
                assertFalse(it.step())
            }
        }
    }
}
