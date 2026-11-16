package me.bookk.site.legal.markdown

sealed interface MarkdownInline {
    data class Text(
        val value: String,
    ) : MarkdownInline

    data class Strong(
        val content: List<MarkdownInline>,
    ) : MarkdownInline

    data class Link(
        val label: String,
        val url: String,
    ) : MarkdownInline
}

sealed interface MarkdownBlock {
    data class Heading(
        val level: Int,
        val content: List<MarkdownInline>,
        val anchor: String? = null,
    ) : MarkdownBlock

    data class Paragraph(
        val content: List<MarkdownInline>,
    ) : MarkdownBlock

    data class BulletList(
        val items: List<List<MarkdownInline>>,
    ) : MarkdownBlock

    data class OrderedList(
        val items: List<List<MarkdownInline>>,
    ) : MarkdownBlock

    data object ThematicBreak : MarkdownBlock

    data class Table(
        val header: List<List<MarkdownInline>>,
        val rows: List<List<List<MarkdownInline>>>,
    ) : MarkdownBlock
}

object Markdown {
    fun parse(source: String): List<MarkdownBlock> {
        return BlockParser(source.lines()).parse()
    }
}

private const val BULLET = "- "
private const val TABLE_EDGE = "|"
private val HEADING = Regex("^#{1,6} .*")
private val HEADING_ANCHOR = Regex("^<a id=\"([^\"]+)\"></a>")
private val NUMBERED = Regex("^\\d+\\. ")
private val THEMATIC_BREAK = Regex("^-{3,}$")
private val TABLE_SEPARATOR = Regex("^\\|[\\s:|-]*-[\\s:|-]*$")

private class BlockParser(
    private val lines: List<String>,
) {
    private var index = 0

    fun parse(): List<MarkdownBlock> {
        val blocks = mutableListOf<MarkdownBlock>()
        while (index < lines.size) {
            if (lines[index].isBlank()) {
                index++
            } else {
                blocks += block(lines[index])
            }
        }
        return blocks
    }

    private fun block(line: String): MarkdownBlock {
        return when {
            HEADING.matches(line) -> heading(line)
            line.startsWith(BULLET) -> bulletList()
            NUMBERED.containsMatchIn(line) -> orderedList()
            THEMATIC_BREAK.matches(line.trim()) -> thematicBreak()
            line.startsWith(TABLE_EDGE) -> table()
            else -> paragraph()
        }
    }

    private fun heading(line: String): MarkdownBlock.Heading {
        index++
        val level = line.takeWhile { it == '#' }.length
        val text = line.drop(level).trim()
        val anchor = HEADING_ANCHOR.find(text)
        val title = anchor?.let { text.removeRange(it.range).trim() } ?: text
        return MarkdownBlock.Heading(
            level,
            InlineParser(title).parse(),
            anchor?.groupValues?.get(1),
        )
    }

    private fun bulletList(): MarkdownBlock.BulletList {
        val items = takeLinesWhile { it.startsWith(BULLET) }
        return MarkdownBlock.BulletList(
            items.map {
                InlineParser(it.removePrefix(BULLET).trim()).parse()
            },
        )
    }

    private fun orderedList(): MarkdownBlock.OrderedList {
        val items = takeLinesWhile { NUMBERED.containsMatchIn(it) }
        return MarkdownBlock.OrderedList(
            items.map {
                InlineParser(it.replaceFirst(NUMBERED, "").trim()).parse()
            },
        )
    }

    private fun thematicBreak(): MarkdownBlock.ThematicBreak {
        index++
        return MarkdownBlock.ThematicBreak
    }

    private fun table(): MarkdownBlock.Table {
        val rows = takeLinesWhile { it.startsWith(TABLE_EDGE) }
            .filterNot { TABLE_SEPARATOR.matches(it.trim()) }
            .map(::cells)
        return MarkdownBlock.Table(rows.first(), rows.drop(1))
    }

    private fun cells(line: String): List<List<MarkdownInline>> {
        return line.trim()
            .removePrefix(TABLE_EDGE)
            .removeSuffix(TABLE_EDGE)
            .split(TABLE_EDGE)
            .map { InlineParser(it.trim()).parse() }
    }

    private fun paragraph(): MarkdownBlock.Paragraph {
        val first = lines[index++]
        val rest = takeLinesWhile { it.isNotBlank() && !startsBlock(it) }
        val text = (listOf(first) + rest).joinToString(" ") { it.trim() }
        return MarkdownBlock.Paragraph(InlineParser(text).parse())
    }

    private fun startsBlock(line: String): Boolean {
        return HEADING.matches(line) ||
            line.startsWith(BULLET) ||
            NUMBERED.containsMatchIn(line) ||
            THEMATIC_BREAK.matches(line.trim()) ||
            line.startsWith(TABLE_EDGE)
    }

    private fun takeLinesWhile(predicate: (String) -> Boolean): List<String> {
        val taken = mutableListOf<String>()
        while (index < lines.size && predicate(lines[index])) {
            taken += lines[index++]
        }
        return taken
    }
}

private const val STRONG = "**"
private const val LINK_MIDDLE = "]("
private val URL_SCHEMES = listOf("https://", "http://")
private const val URL_TRAILING_PUNCTUATION = ".,;:!?*_~'\"»”"

private class InlineParser(
    private val text: String,
) {
    private val inlines = mutableListOf<MarkdownInline>()
    private val pending = StringBuilder()
    private var index = 0

    fun parse(): List<MarkdownInline> {
        while (index < text.length) {
            val token = strong() ?: link() ?: bareUrl()
            if (token == null) {
                pending.append(text[index++])
            } else {
                flushText()
                inlines += token
            }
        }
        flushText()
        return inlines
    }

    private fun strong(): MarkdownInline? {
        if (!text.startsWith(STRONG, index)) return null
        val start = index + STRONG.length
        val end = text.indexOf(STRONG, start)
        if (end <= start) return null
        index = end + STRONG.length
        return MarkdownInline.Strong(InlineParser(text.substring(start, end)).parse())
    }

    private fun link(): MarkdownInline? {
        if (text[index] != '[') return null
        val middle = text.indexOf(LINK_MIDDLE, index)
        if (middle < 0) return null
        val end = text.indexOf(')', middle + LINK_MIDDLE.length)
        if (end < 0) return null
        val link = MarkdownInline.Link(
            text.substring(index + 1, middle),
            text.substring(middle + LINK_MIDDLE.length, end),
        )
        index = end + 1
        return link
    }

    private fun bareUrl(): MarkdownInline? {
        if (URL_SCHEMES.none { text.startsWith(it, index) } || followsWordCharacter()) return null
        val end = text.indexOfFirst(index) { it.isWhitespace() }
        val url = trimUrl(text.substring(index, end))
        index += url.length
        return MarkdownInline.Link(url, url)
    }

    private fun followsWordCharacter(): Boolean {
        return index > 0 && text[index - 1].isLetterOrDigit()
    }

    private fun flushText() {
        if (pending.isEmpty()) return
        inlines += MarkdownInline.Text(pending.toString())
        pending.clear()
    }
}

private fun String.indexOfFirst(from: Int, predicate: (Char) -> Boolean): Int {
    return (from until length).firstOrNull { predicate(this[it]) } ?: length
}

private fun trimUrl(candidate: String): String {
    var url = candidate
    while (url.isNotEmpty() && endsWithTrailingCharacter(url)) {
        url = url.dropLast(1)
    }
    return url
}

private fun endsWithTrailingCharacter(url: String): Boolean {
    val last = url.last()
    return last in URL_TRAILING_PUNCTUATION ||
        (last == ')' && url.count { it == ')' } > url.count { it == '(' })
}
