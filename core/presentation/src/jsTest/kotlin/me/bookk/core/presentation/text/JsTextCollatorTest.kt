package me.bookk.core.presentation.text

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class JsTextCollatorTest {

    @AfterTest
    fun tearDown() {
        StringDesc.localeType = StringDesc.LocaleType.System
    }

    @Test
    fun `sorts accents next to their base letter and numbers by value`() = runUnitTest {
        given()
        StringDesc.localeType = StringDesc.LocaleType.Custom("en")
        val collator = JsTextCollator()

        whenn()
        val sorted = listOf(
            "item10",
            "Éclair",
            "item2",
            "apple",
            "zebra",
        ).sortedWith(collator.comparator())

        then()
        assertEquals(listOf("apple", "Éclair", "item2", "item10", "zebra"), sorted)
    }
}
