package me.bookk.database.migration

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.AppDatabase_AutoMigration_23_24_Impl
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LegalDocumentStatusMigrationTest {
    @BeforeTest
    fun setUp() {
        prepareSqliteTestDriver()
    }

    private class Fixture {
        val connection = BundledSQLiteDriver().open(":memory:").apply {
            execSQL(
                "CREATE TABLE user_profile (id TEXT NOT NULL PRIMARY KEY, " +
                    "firstName TEXT NOT NULL, lastName TEXT NOT NULL, " +
                    "email TEXT NOT NULL, phone TEXT)",
            )
            execSQL(
                "INSERT INTO user_profile VALUES " +
                    "('00000000-0000-0000-0000-000000000001', 'Anna', 'Smith', 'anna@example.com', NULL)",
            )
        }
        val sut = AppDatabase_AutoMigration_23_24_Impl()
    }

    @Test
    fun `adds the legal status cache without changing existing user data`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.connection.use { connection ->
            whenn()
            fixture.sut.migrate(connection)

            then()
            connection.prepare("SELECT firstName, lastName, email FROM user_profile").use {
                assertTrue(it.step())
                assertEquals("Anna", it.getText(0))
                assertEquals("Smith", it.getText(1))
                assertEquals("anna@example.com", it.getText(2))
                assertFalse(it.step())
            }
            connection.prepare(
                "SELECT document, version, url, effectiveAt, acceptedAt FROM legal_document_status",
            ).use {
                assertFalse(it.step())
            }
        }
    }
}
