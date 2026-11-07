package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.settings.domain.api.entity.AcceptedLegalDocuments
import me.bookk.feature.settings.domain.datasource.LegalDocumentsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class AcceptLegalDocumentsImplTest {
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture {
        val dataSource = mock<LegalDocumentsDataSource>()
        val sut = AcceptLegalDocumentsImpl(dataSource)
    }

    @Test
    fun `sends exactly the versions accepted by the caller`() = runUnitTest {
        given()
        val fixture = Fixture()
        val documents = AcceptedLegalDocuments(privacyPolicy = "privacy-v3")
        everySuspend { fixture.dataSource.accept(documents) } returns Unit

        whenn()
        fixture.sut(documents)

        then()
        dev.mokkery.verifySuspend { fixture.dataSource.accept(documents) }
    }

    @Test
    fun `maps an unknown version without claiming acceptance succeeded`() = runUnitTest {
        given()
        val fixture = Fixture()
        val documents = AcceptedLegalDocuments(termsOfService = "unknown")
        everySuspend { fixture.dataSource.accept(documents) } throws
            me.bookk.core.domain.entity.Error.BusinessError(100003, "unknown version")

        whenn()
        val result = runCatching { fixture.sut(documents) }

        then()
        assertFailsWith<
            me.bookk.feature.settings.domain.api.AcceptLegalDocuments.Error.UnknownVersion,
            > {
            result.getOrThrow()
        }
    }

    @Test
    fun `preserves unrelated errors`() = runUnitTest {
        given()
        val fixture = Fixture()
        val documents = AcceptedLegalDocuments()
        everySuspend { fixture.dataSource.accept(documents) } throws IllegalStateException("failed")

        whenn()
        val result = runCatching { fixture.sut(documents) }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }
}
