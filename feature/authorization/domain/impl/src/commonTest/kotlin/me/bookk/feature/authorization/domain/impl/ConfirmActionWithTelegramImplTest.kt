package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import kotlin.test.Test
import kotlin.test.assertEquals

class ConfirmActionWithTelegramImplTest {
    private class Fixture {
        val initData = "auth_date=1&start_param=op_delete-account_00&hash=00"
        val dataSource = mock<AuthorizationDataSource>()
        val sut = ConfirmActionWithTelegramImpl(dataSource)
    }

    @Test
    fun `returns the confirmation token for the signed launch data`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.confirmActionWithTelegram(fixture.initData) } returns
            "single-use"

        whenn()
        val token = fixture.sut(fixture.initData)

        then()
        assertEquals("single-use", token)
    }
}
