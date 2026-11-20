package me.bookk.site.legal.markdown

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.site.legal.markdown.MarkdownBlock.BulletList
import me.bookk.site.legal.markdown.MarkdownBlock.Heading
import me.bookk.site.legal.markdown.MarkdownBlock.OrderedList
import me.bookk.site.legal.markdown.MarkdownBlock.Paragraph
import me.bookk.site.legal.markdown.MarkdownBlock.Table
import me.bookk.site.legal.markdown.MarkdownBlock.ThematicBreak
import me.bookk.site.legal.markdown.MarkdownInline.Link
import me.bookk.site.legal.markdown.MarkdownInline.Strong
import me.bookk.site.legal.markdown.MarkdownInline.Text
import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownTest {

    @Test
    fun `parses headings with their level`() = runUnitTest {
        given()
        val source = "# Title\n\n## 1. Section"

        whenn()
        val blocks = Markdown.parse(source)

        then()
        assertEquals(
            listOf(
                Heading(1, listOf(Text("Title"))),
                Heading(2, listOf(Text("1. Section"))),
            ),
            blocks,
        )
    }

    @Test
    fun `joins consecutive lines into one paragraph and splits paragraphs on blank lines`() =
        runUnitTest {
            given()
            val source = "First line\nsecond line\n\nNext paragraph"

            whenn()
            val blocks = Markdown.parse(source)

            then()
            assertEquals(
                listOf(
                    Paragraph(listOf(Text("First line second line"))),
                    Paragraph(listOf(Text("Next paragraph"))),
                ),
                blocks,
            )
        }

    @Test
    fun `parses consecutive bullet lines as one list`() = runUnitTest {
        given()
        val source = "Intro\n\n- first\n- second\n\nOutro"

        whenn()
        val blocks = Markdown.parse(source)

        then()
        assertEquals(
            listOf(
                Paragraph(listOf(Text("Intro"))),
                BulletList(listOf(listOf(Text("first")), listOf(Text("second")))),
                Paragraph(listOf(Text("Outro"))),
            ),
            blocks,
        )
    }

    @Test
    fun `parses a table header and rows without the separator row`() = runUnitTest {
        given()
        val source = "| Data | Kept |\n|---|---|\n| Logs | 14 days |\n| Backups | 30 days |"

        whenn()
        val blocks = Markdown.parse(source)

        then()
        assertEquals(
            listOf(
                Table(
                    header = listOf(listOf(Text("Data")), listOf(Text("Kept"))),
                    rows = listOf(
                        listOf(listOf(Text("Logs")), listOf(Text("14 days"))),
                        listOf(listOf(Text("Backups")), listOf(Text("30 days"))),
                    ),
                ),
            ),
            blocks,
        )
    }

    @Test
    fun `parses bold text inside a paragraph`() = runUnitTest {
        given()
        val source = "We are **the controller** here."

        whenn()
        val blocks = Markdown.parse(source)

        then()
        assertEquals(
            listOf(
                Paragraph(
                    listOf(Text("We are "), Strong(listOf(Text("the controller"))), Text(" here.")),
                ),
            ),
            blocks,
        )
    }

    @Test
    fun `keeps an unclosed bold marker as text`() = runUnitTest {
        given()
        val source = "A ** marker"

        whenn()
        val blocks = Markdown.parse(source)

        then()
        assertEquals(listOf(Paragraph(listOf(Text("A ** marker")))), blocks)
    }

    @Test
    fun `parses an inline link`() = runUnitTest {
        given()
        val source = "See its [privacy policy](https://example.com/privacy)."

        whenn()
        val blocks = Markdown.parse(source)

        then()
        assertEquals(
            listOf(
                Paragraph(
                    listOf(
                        Text("See its "),
                        Link("privacy policy", "https://example.com/privacy"),
                        Text("."),
                    ),
                ),
            ),
            blocks,
        )
    }

    @Test
    fun `links a bare url without its trailing punctuation`() = runUnitTest {
        given()
        val source = "Published at https://bookkme.com/dpa. Questions go first."

        whenn()
        val blocks = Markdown.parse(source)

        then()
        assertEquals(
            listOf(
                Paragraph(
                    listOf(
                        Text("Published at "),
                        Link("https://bookkme.com/dpa", "https://bookkme.com/dpa"),
                        Text(". Questions go first."),
                    ),
                ),
            ),
            blocks,
        )
    }

    @Test
    fun `leaves an enclosing parenthesis out of a bare url`() = runUnitTest {
        given()
        val source = "clauses (https://eur-lex.europa.eu/TXT/?uri=CELEX:32021D0914) apply"

        whenn()
        val blocks = Markdown.parse(source)

        then()
        val url = "https://eur-lex.europa.eu/TXT/?uri=CELEX:32021D0914"
        assertEquals(
            listOf(Paragraph(listOf(Text("clauses ("), Link(url, url), Text(") apply")))),
            blocks,
        )
    }

    @Test
    fun `keeps balanced parentheses inside a bare url`() = runUnitTest {
        given()
        val source = "See https://en.wikipedia.org/wiki/Passkey_(authentication) now"

        whenn()
        val blocks = Markdown.parse(source)

        then()
        val url = "https://en.wikipedia.org/wiki/Passkey_(authentication)"
        assertEquals(listOf(Paragraph(listOf(Text("See "), Link(url, url), Text(" now")))), blocks)
    }

    @Test
    fun `parses inline formatting inside list items and table cells`() = runUnitTest {
        given()
        val list = "- **Bold** item"
        val row = "| Postmark | [DPA](https://postmarkapp.com/dpa) |"
        val table = "| Provider | Terms |\n|---|---|\n$row"
        val source = "$list\n\n$table"

        whenn()
        val blocks = Markdown.parse(source)

        then()
        assertEquals(
            listOf(
                BulletList(
                    listOf(
                        listOf(Strong(listOf(Text("Bold"))), Text(" item")),
                    ),
                ),
                Table(
                    header = listOf(listOf(Text("Provider")), listOf(Text("Terms"))),
                    rows = listOf(
                        listOf(
                            listOf(Text("Postmark")),
                            listOf(Link("DPA", "https://postmarkapp.com/dpa")),
                        ),
                    ),
                ),
            ),
            blocks,
        )
    }

    @Test
    fun `reads a leading html anchor as the heading anchor`() = runUnitTest {
        given()
        val source = "## <a id=\"section-1\"></a>1. Who we are"

        whenn()
        val blocks = Markdown.parse(source)

        then()
        assertEquals(listOf(Heading(2, listOf(Text("1. Who we are")), "section-1")), blocks)
    }

    @Test
    fun `parses consecutive numbered lines as one ordered list`() = runUnitTest {
        given()
        val source = "1. [Who we are](#section-1)\n2. [Our two roles](#section-2)"

        whenn()
        val blocks = Markdown.parse(source)

        then()
        assertEquals(
            listOf(
                OrderedList(
                    listOf(
                        listOf(Link("Who we are", "#section-1")),
                        listOf(Link("Our two roles", "#section-2")),
                    ),
                ),
            ),
            blocks,
        )
    }

    @Test
    fun `ends a paragraph at a numbered line`() = runUnitTest {
        given()
        val source = "Contents\n1. First"

        whenn()
        val blocks = Markdown.parse(source)

        then()
        assertEquals(
            listOf(Paragraph(listOf(Text("Contents"))), OrderedList(listOf(listOf(Text("First"))))),
            blocks,
        )
    }

    @Test
    fun `parses a line of three dashes as a thematic break`() = runUnitTest {
        given()
        val source = "Intro\n\n---\n\n## Next"

        whenn()
        val blocks = Markdown.parse(source)

        then()
        assertEquals(
            listOf(
                Paragraph(listOf(Text("Intro"))),
                ThematicBreak,
                Heading(2, listOf(Text("Next"))),
            ),
            blocks,
        )
    }
}
