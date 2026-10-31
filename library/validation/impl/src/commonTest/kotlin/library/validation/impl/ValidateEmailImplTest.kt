package library.validation.impl

import library.validation.api.ValidateEmail
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class ValidateEmailImplTest {

    private class Fixture {
        val sut = ValidateEmailImpl()
    }

    @Test
    fun `accepts a well formed email`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut("john@example.com")

        then()
        assertEquals(ValidateEmail.Result.Valid, result)
    }

    @Test
    fun `rejects a malformed email`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut("john@")

        then()
        assertEquals(ValidateEmail.Result.Invalid.Format, result)
    }

    @Test
    fun `accepts a well formed email surrounded by whitespace`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut(" john@example.com \n")

        then()
        assertEquals(ValidateEmail.Result.Valid, result)
    }
}
