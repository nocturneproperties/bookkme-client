package me.bookk.database.business

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.database.ExternalChangeInvalidator
import me.bookk.database.business.migration.prepareSqliteTestDriver
import me.bookk.database.entity.BusinessEntity
import me.bookk.database.relation.BusinessLocal
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid

class ExternalChangeInvalidatorTest {

    private val directory = Files.createTempDirectory("bookkme-invalidator-test").toFile()

    @BeforeTest
    fun setUp() {
        prepareSqliteTestDriver()
    }

    @AfterTest
    fun tearDown() {
        directory.deleteRecursively()
    }

    private inner class Fixture {
        private val file = File(directory, "shared.db")
        val observingTab = openDatabase()
        val writingTab = openDatabase()
        val sut = ExternalChangeInvalidator(observingTab)

        private fun openDatabase(): BusinessDatabase {
            return Room.databaseBuilder<BusinessDatabase>(name = file.absolutePath)
                .setDriver(BundledSQLiteDriver())
                .build()
        }

        fun close() {
            observingTab.close()
            writingTab.close()
        }
    }

    @Test
    fun `re-emits observed queries after another connection commits`() = runUnitTest {
        given()
        val fixture = Fixture()
        val emissions = Channel<List<BusinessLocal>>(Channel.UNLIMITED)
        val collecting = launch(Dispatchers.Default) {
            fixture.observingTab.businessDao().observeAllBusinesses().collect { emissions.send(it) }
        }
        emissions.awaitNext()
        fixture.sut.invalidateIfChangedElsewhere()
        fixture.writingTab.businessDao().upsertBusiness(stubBusiness())

        whenn()
        fixture.sut.invalidateIfChangedElsewhere()

        then()
        assertEquals(1, emissions.awaitNext().size)
        collecting.cancel()
        fixture.close()
    }

    @Test
    fun `reports a commit made by another connection`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.invalidateIfChangedElsewhere()
        fixture.writingTab.businessDao().upsertBusiness(stubBusiness())

        whenn()
        val result = fixture.sut.invalidateIfChangedElsewhere()

        then()
        assertTrue(result)
        fixture.close()
    }

    @Test
    fun `only records the data version on the first check`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.writingTab.businessDao().upsertBusiness(stubBusiness())

        whenn()
        val result = fixture.sut.invalidateIfChangedElsewhere()

        then()
        assertFalse(result)
        fixture.close()
    }

    @Test
    fun `ignores a commit made by its own database`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.sut.invalidateIfChangedElsewhere()
        fixture.observingTab.businessDao().upsertBusiness(stubBusiness())

        whenn()
        val result = fixture.sut.invalidateIfChangedElsewhere()

        then()
        assertFalse(result)
        fixture.close()
    }

    private suspend fun <T> Channel<T>.awaitNext(): T {
        return withContext(Dispatchers.Default) {
            withTimeout(5.seconds) { receive() }
        }
    }

    private fun stubBusiness(): BusinessEntity {
        return BusinessEntity(
            id = Uuid.random(),
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
    }
}
