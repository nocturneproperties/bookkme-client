package build_src.site

import org.gradle.api.GradleException
import java.io.File
import java.security.MessageDigest
import java.time.Instant
import java.time.format.DateTimeParseException

internal class LegalText(
    val document: String,
    val version: String,
    val effectiveAt: String,
    val language: String,
    val markdown: String,
)

internal class LegalTextsReader(
    private val root: File,
) {
    private val errors = mutableListOf<String>()

    fun read(): List<LegalText> {
        val texts = documentDirectories().flatMap(::readDocument)
        verifyLock()
        if (errors.isNotEmpty()) {
            throw GradleException(
                errors.joinToString(
                    separator = "\n",
                    prefix = "Invalid published legal texts in $root:\n",
                ),
            )
        }
        return texts.sortedWith(compareBy({ it.document }, { it.version }, { it.language }))
    }

    fun publishedVersionPaths(): List<String> {
        val locked = readLock().keys
        return documentDirectories()
            .flatMap(::versionDirectories)
            .filter { version -> VERSION_FILES.all { "${version.relative()}/$it" in locked } }
            .filter { version ->
                version.listFiles().orEmpty().map { it.name }.toSet() ==
                    VERSION_FILES
            }
            .map { "/${it.relative()}" }
    }

    private fun documentDirectories(): List<File> {
        val directories = root.listFiles().orEmpty().filter { it.isDirectory }.sortedBy { it.name }
        directories.filter { it.name !in DOCUMENTS }.forEach {
            errors +=
                "unknown document folder '${it.name}', expected one of $DOCUMENTS"
        }
        return directories.filter { it.name in DOCUMENTS }
    }

    private fun readDocument(document: File): List<LegalText> {
        return versionDirectories(document).flatMap(::readVersion)
    }

    private fun versionDirectories(document: File): List<File> {
        val versions = document.listFiles().orEmpty().sortedBy { it.name }
        versions.filterNot { it.isDirectory && VERSION_NAME.matches(it.name) }
            .forEach {
                errors += "'${it.relative()}' is not a version folder named like 2026-12-01"
            }
        return versions.filter { it.isDirectory && VERSION_NAME.matches(it.name) }
    }

    private fun readVersion(version: File): List<LegalText> {
        val names = version.listFiles().orEmpty().map { it.name }.toSet()
        if (names != VERSION_FILES) {
            errors += "'${version.relative()}' must contain exactly $VERSION_FILES, found $names"
            return emptyList()
        }
        val effectiveAt = readEffectiveAt(version) ?: return emptyList()
        return LANGUAGES.map { language ->
            LegalText(
                version.parentFile.name,
                version.name,
                effectiveAt,
                language,
                readMarkdown(version.resolve("$language.md")),
            )
        }
    }

    private fun readEffectiveAt(version: File): String? {
        val value = version.resolve(EFFECTIVE_AT_FILE).readText().trim()
        return try {
            Instant.parse(value).toString()
        } catch (exception: DateTimeParseException) {
            errors +=
                "'${version.relative()}/$EFFECTIVE_AT_FILE' is not a UTC time like 2026-12-01T00:00:00Z: $value"
            null
        }
    }

    private fun readMarkdown(file: File): String {
        val markdown = file.readText()
        if (PLACEHOLDER in
            markdown
        ) {
            errors += "'${file.relative()}' still contains a $PLACEHOLDER placeholder"
        }
        return markdown
    }

    private fun verifyLock() {
        val locked = readLock()
        val published = publishedFiles()
        published.filterKeys { it !in locked }.keys.forEach {
            errors += "'$it' is not in $LOCK_FILE; publish versions with legal/import.sh"
        }
        locked.filterKeys { it !in published }.keys.forEach {
            errors += "'$it' was published and must not be deleted"
        }
        published.filter { (path, hash) ->
            locked[path] != null && locked[path] != hash
        }.keys.forEach {
            errors += "'$it' changed after publication; publish a new version instead"
        }
    }

    private fun readLock(): Map<String, String> {
        val lock = root.resolve(LOCK_FILE)
        if (!lock.exists()) return emptyMap()
        return lock.readLines()
            .filter { it.isNotBlank() }
            .associate { line -> line.substringAfter("  ") to line.substringBefore("  ") }
    }

    private fun publishedFiles(): Map<String, String> {
        return root.walkTopDown()
            .filter { it.isFile && it.parentFile.parentFile?.parentFile == root }
            .associate { it.relative() to it.sha256() }
    }

    private fun File.relative(): String {
        return relativeTo(root).invariantSeparatorsPath
    }

    private companion object {
        val DOCUMENTS = setOf("privacy", "terms", "dpa")
        val LANGUAGES = listOf("en", "uk", "pl")
        const val EFFECTIVE_AT_FILE = "effective-at.txt"
        val VERSION_FILES = LANGUAGES.map { "$it.md" }.toSet() + EFFECTIVE_AT_FILE
        val VERSION_NAME = Regex("[A-Za-z0-9._-]{1,32}")
        const val LOCK_FILE = "published.sha256"
        const val PLACEHOLDER = "{{"
    }
}

object LegalVersionPaths {
    fun read(legalDirectory: File): List<String> {
        return LegalTextsReader(legalDirectory).publishedVersionPaths()
    }
}

private fun File.sha256(): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(readBytes())
        .joinToString("") { "%02x".format(it) }
}

internal object LegalTextsWriter {
    fun write(texts: List<LegalText>): String {
        return buildString {
            appendLine("package me.bookk.site.legal")
            appendLine()
            appendLine("internal val publishedLegalTexts: List<PublishedLegalText> = listOf(")
            texts.forEach { append(record(it)) }
            appendLine(")")
        }
    }

    private fun record(text: LegalText): String {
        return buildString {
            appendLine("    PublishedLegalText(")
            appendLine("        document = ${text.document.kotlinLiteral()},")
            appendLine("        version = ${text.version.kotlinLiteral()},")
            appendLine("        effectiveAt = ${text.effectiveAt.kotlinLiteral()},")
            appendLine("        language = ${text.language.kotlinLiteral()},")
            appendLine("        markdown = ${text.markdown.kotlinLiteral()},")
            appendLine("    ),")
        }
    }
}
