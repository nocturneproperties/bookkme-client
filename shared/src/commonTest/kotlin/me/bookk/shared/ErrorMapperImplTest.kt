package me.bookk.shared

import dev.icerock.moko.resources.desc.desc
import me.bookk.core.domain.entity.Error
import me.bookk.core.presentation.error.ErrorDescription
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.DesignSystem
import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorMapperImplTest {

    private class Fixture {
        val sut = ErrorMapperImpl()
    }

    @Test
    fun `describes no connection as a connection issue`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut.mapToDescription(Error.NoConnectionError(Exception()))

        then()
        assertEquals(
            ErrorDescription(
                title = DesignSystem.strings.error_connection_title.desc(),
                message = DesignSystem.strings.error_connection_subtitle.desc()
            ),
            result
        )
    }

    @Test
    fun `describes an internal server error as a server issue`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut.mapToDescription(Error.InternalServerError(Exception()))

        then()
        assertEquals(
            ErrorDescription(
                title = DesignSystem.strings.error_server_title.desc(),
                message = DesignSystem.strings.error_server.desc()
            ),
            result
        )
    }

    @Test
    fun `describes a bad request as a server issue`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut.mapToDescription(Error.BadRequest(Exception()))

        then()
        assertEquals(DesignSystem.strings.error_server_title.desc(), result.title)
    }

    @Test
    fun `describes a business error with its own message`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut.mapToDescription(Error.BusinessError(errorCode = 42, message = "Slot taken"))

        then()
        assertEquals(
            ErrorDescription(
                title = DesignSystem.strings.error_generic_title.desc(),
                message = "Slot taken".desc()
            ),
            result
        )
    }

    @Test
    fun `describes an unknown error as unexpected`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut.mapToDescription(IllegalStateException())

        then()
        assertEquals(
            ErrorDescription(
                title = DesignSystem.strings.error_generic_title.desc(),
                message = DesignSystem.strings.error_unexpected.desc()
            ),
            result
        )
    }
}
