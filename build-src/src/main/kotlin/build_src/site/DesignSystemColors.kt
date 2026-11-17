package build_src.site

import org.gradle.api.GradleException
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class DesignSystemColors private constructor(
    private val colors: Map<String, String>,
) {
    fun opaque(name: String): String {
        val value = colors[name]
            ?: throw GradleException("Design system color '$name' is not defined in $PATH")
        if (!OPAQUE.matches(value)) {
            throw GradleException(
                "Design system color '$name' is $value, expected an opaque #RRGGBB color",
            )
        }
        return value.uppercase()
    }

    companion object {
        const val PATH = "designsystem/src/commonMain/moko-resources/colors/colors.xml"
        private val OPAQUE = Regex("#[0-9A-Fa-f]{6}")

        fun read(rootDirectory: File): DesignSystemColors {
            return parse(rootDirectory.resolve(PATH).readText())
        }

        fun parse(xml: String): DesignSystemColors {
            val document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(xml.byteInputStream())
            val elements = document.getElementsByTagName("color")
            val colors = (0 until elements.length)
                .map { elements.item(it) as Element }
                .associate { it.getAttribute("name") to it.textContent.trim() }
            return DesignSystemColors(colors)
        }
    }
}
