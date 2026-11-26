package me.bookk.core.domain.launch

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class LaunchParameterTest {
    private val challenge = "0123456789abcdef0123456789abcdef"

    @Test
    fun `parses an account deletion confirmation`() = runUnitTest {
        given()
        val value = "op_delete-account_$challenge"

        whenn()
        val result = LaunchParameter.parse(value)

        then()
        assertEquals(LaunchParameter.Confirmation(ConfirmableAction.DELETE_ACCOUNT), result)
    }

    @Test
    fun `parses a personal data export confirmation`() = runUnitTest {
        given()
        val value = "op_export-personal_$challenge"

        whenn()
        val result = LaunchParameter.parse(value)

        then()
        assertEquals(LaunchParameter.Confirmation(ConfirmableAction.EXPORT_PERSONAL_DATA), result)
    }

    @Test
    fun `treats an unknown operation kind as unsupported`() = runUnitTest {
        given()
        val value = "op_delete-business_$challenge"

        whenn()
        val result = LaunchParameter.parse(value)

        then()
        assertEquals(LaunchParameter.UnsupportedOperation, result)
    }

    @Test
    fun `treats a known kind with an uppercase challenge as unsupported`() = runUnitTest {
        given()
        val value = "op_delete-account_${challenge.uppercase()}"

        whenn()
        val result = LaunchParameter.parse(value)

        then()
        assertEquals(LaunchParameter.UnsupportedOperation, result)
    }

    @Test
    fun `treats a known kind with a short challenge as unsupported`() = runUnitTest {
        given()
        val value = "op_export-personal_${challenge.drop(1)}"

        whenn()
        val result = LaunchParameter.parse(value)

        then()
        assertEquals(LaunchParameter.UnsupportedOperation, result)
    }

    @Test
    fun `treats a known kind without a challenge as unsupported`() = runUnitTest {
        given()
        val value = "op_delete-account"

        whenn()
        val result = LaunchParameter.parse(value)

        then()
        assertEquals(LaunchParameter.UnsupportedOperation, result)
    }

    @Test
    fun `treats the bare operation prefix as unsupported`() = runUnitTest {
        given()
        val value = "op_"

        whenn()
        val result = LaunchParameter.parse(value)

        then()
        assertEquals(LaunchParameter.UnsupportedOperation, result)
    }

    @Test
    fun `parses a business id`() = runUnitTest {
        given()
        val businessId = Uuid.random()

        whenn()
        val result = LaunchParameter.parse(businessId.toString())

        then()
        assertEquals(LaunchParameter.Business(businessId), result)
    }

    @Test
    fun `parses a business link name`() = runUnitTest {
        given()
        val value = "my-salon"

        whenn()
        val result = LaunchParameter.parse(value)

        then()
        assertEquals(LaunchParameter.BusinessLink("my-salon"), result)
    }

    @Test
    fun `parses an empty value as no parameter`() = runUnitTest {
        given()
        val value = ""

        whenn()
        val result = LaunchParameter.parse(value)

        then()
        assertEquals(LaunchParameter.None, result)
    }

    @Test
    fun `parses a blank value as no parameter`() = runUnitTest {
        given()
        val value = "  "

        whenn()
        val result = LaunchParameter.parse(value)

        then()
        assertEquals(LaunchParameter.None, result)
    }
}
