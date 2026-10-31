package library.validation.impl

import library.validation.api.ValidateName
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class ValidateNameImplTest {

    private class Fixture {
        val sut = ValidateNameImpl()
    }

    @Test
    fun `accepts a name of minimal length`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut("Jo")

        then()
        assertEquals(ValidateName.Result.Valid, result)
    }

    @Test
    fun `rejects a name shorter than minimal length`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut("J")

        then()
        assertEquals(ValidateName.Result.Invalid.Length, result)
    }

    @Test
    fun `rejects a name that is too short once surrounding whitespace is ignored`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut("  J ")

        then()
        assertEquals(ValidateName.Result.Invalid.Length, result)
    }
}
