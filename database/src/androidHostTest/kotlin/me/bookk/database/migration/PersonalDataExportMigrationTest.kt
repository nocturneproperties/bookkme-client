package me.bookk.database.migration

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.AppDatabase_AutoMigration_24_25_Impl
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PersonalDataExportMigrationTest {
    @BeforeTest
    fun setUp() {
        prepareSqliteTestDriver()
    }

    private class Fixture {
        val connection = BundledSQLiteDriver().open(":memory:").apply {
            execSQL(
                "CREATE TABLE user_profile (id TEXT NOT NULL PRIMARY KEY, firstName TEXT NOT NULL)",
            )
            execSQL("INSERT INTO user_profile VALUES ('user-id', 'Anna')")
        }
        val sut = AppDatabase_AutoMigration_24_25_Impl()
    }

    @Test
    fun `adds an empty export cache while preserving existing user data`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.connection.use { connection ->
            whenn()
            fixture.sut.migrate(connection)

            then()
            connection.prepare("SELECT firstName FROM user_profile WHERE id = 'user-id'").use {
                assertTrue(it.step())
                assertEquals("Anna", it.getText(0))
                assertFalse(it.step())
            }
            connection.prepare("SELECT * FROM personal_data_export").use {
                assertFalse(it.step())
            }
        }
    }

    @Test
    fun `stores a confirmed never requested status with nullable timestamps`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.connection.use { connection ->
            fixture.sut.migrate(connection)

            whenn()
            connection.execSQL(
                "INSERT INTO personal_data_export " +
                    "(id, status, requestedAt, expiresAt, availableAgainAt) " +
                    "VALUES (0, 'NONE', NULL, NULL, NULL)",
            )

            then()
            connection.prepare("SELECT * FROM personal_data_export WHERE id = 0").use {
                assertTrue(it.step())
                assertEquals("NONE", it.getText(1))
                assertTrue(it.isNull(2))
                assertTrue(it.isNull(3))
                assertTrue(it.isNull(4))
                assertFalse(it.step())
            }
        }
    }
}
