package build_src.site

import org.gradle.api.GradleException
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class MokoStrings private constructor(
    private val file: File,
    private val values: Map<String, String>,
) {
    fun value(key: String): String {
        return values[key] ?: throw GradleException("String '$key' is missing in $file")
    }

    companion object {
        fun read(file: File): MokoStrings {
            val strings = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(file)
                .getElementsByTagName("string")
            val values = (0 until strings.length)
                .map { strings.item(it) as Element }
                .associate { it.getAttribute("name") to it.textContent.unescaped() }
            return MokoStrings(file, values)
        }

        private fun String.unescaped(): String {
            return replace("\\'", "'")
                .replace("\\\"", "\"")
                .replace("\\n", "\n")
        }
    }
}
