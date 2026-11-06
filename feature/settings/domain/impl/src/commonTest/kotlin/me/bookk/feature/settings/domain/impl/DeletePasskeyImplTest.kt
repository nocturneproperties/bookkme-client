package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.settings.domain.datasource.passkey.PasskeySettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class DeletePasskeyImplTest {

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
        val dataSource = mock<PasskeySettingsDataSource>()
        val sut = DeletePasskeyImpl(dataSource)
    }

    @Test
    fun `calls deletePasskey with correct id`() = runUnitTest {
        given()
        val fixture = Fixture()
        val id = Uuid.random()
        everySuspend { fixture.dataSource.deletePasskey(id) } returns Unit

        whenn()
        fixture.sut(id)

        then()
        verifySuspend { fixture.dataSource.deletePasskey(id) }
    }

    @Test
    fun `maps backend error 9 to LastPasskey`() = runUnitTest {
        given()
        val fixture = Fixture()

        everySuspend { fixture.dataSource.deletePasskey(any()) } throws
            me.bookk.core.domain.entity.Error.BusinessError(9, "rejected")

        whenn()
        val result = runCatching { fixture.sut(Uuid.random()) }

        then()
        assertFailsWith<me.bookk.feature.settings.domain.api.DeletePasskey.Error.LastPasskey> {
            result.getOrThrow()
        }
    }
}
