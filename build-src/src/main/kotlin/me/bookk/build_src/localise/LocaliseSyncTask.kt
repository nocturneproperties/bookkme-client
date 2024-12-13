package me.bookk.build_src.localise

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.net.HttpURLConnection
import java.net.URI


open class LocaliseSyncTask : DefaultTask() {

    private lateinit var extension: LocaliseExtension

    init {
        project.afterEvaluate {
            extension = extensions.getByType()

            doLast {
                executeNow(extension)
            }
        }
    }

    private fun executeNow(extension: LocaliseExtension) {
        if (extension.configs.size == 0) {
            throw GradleException("Could not find any Localise Configs.")
        }

        extension.configs.forEach {
            when(it.format) {
                "xcode" -> setupTaskIOS(it)
                "android" -> setupTask(it)
            }
        }

        println()
        println("--------------------------------------")
        println("Loco text strings updated successfully!")
        println("--------------------------------------")
    }

    private fun setupTask(config: LocaliseConfig) {
        val requestParams = buildString {
            append("?no-comments=${config.hideComments}")
            append("&format=${config.format}")
            config.tag?.let { append("&filter=${it}") }
            config.index?.let { append("&index=${it}") }
            config.status?.let { append("&status=${it}") }
            config.fallbackLang?.let { append("&fallback=${it}") }
            if (config.orderByAssetId) {
                append("&order=id")
            }
        }

        for (language in config.languages) {
            val baseUrl = URI("${config.locoBaseUrl}/${language}.xml${requestParams}").toURL()
            val connection = (baseUrl.openConnection() as HttpURLConnection).apply {
                addRequestProperty("Authorization", "Loco ${config.apiKey}")
                addRequestProperty("Accept-Charset", "utf-8")
                doOutput = true
                requestMethod = "GET"
            }
            val response = String(connection.inputStream.readAllBytes(), Charsets.UTF_8).let {
                var result = it
                config.placeholderPattern?.let { regex ->
                    result = result.replace(Regex(regex), "%s")
                }
                config.resourceNamePrefix?.let { prefix ->
                    result = result
                        .replace("<string name=\"", "<string name=\"$prefix")
                        .replace("<plurals name=\"", "<plurals name=\"$prefix")
                }
                val wrongXmlPrefix = "<?xml version=\"1.0\" encoding=\"utf8\"?>"
                val correctXmlPrefix = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                if (result.startsWith(wrongXmlPrefix)) {
                    result = result.replace(wrongXmlPrefix, correctXmlPrefix)
                }
                result
            }

            when (language) {
                config.defaultLang -> saveFileAndroid(config, response, "base")
                else -> saveFileAndroid(config, response, language)
            }
        }
    }

    private fun setupTaskIOS(config: LocaliseConfig) {
        val requestParams = buildString {
            append("?no-comments=${config.hideComments}")
            append("&format=${config.format}")
            config.tag?.let { append("&filter=${it}") }
            config.index?.let { append("&index=${it}") }
            config.status?.let { append("&status=${it}") }
            config.fallbackLang?.let { append("&fallback=${it}") }
            if (config.orderByAssetId) {
                append("&order=id")
            }
        }

        for (language in config.languages) {
            val baseUrl = URI("${config.locoBaseUrl}/${language}.strings${requestParams}").toURL()
            val connection = (baseUrl.openConnection() as HttpURLConnection).apply {
                addRequestProperty("Authorization", "Loco ${config.apiKey}")
                addRequestProperty("Accept-Charset", "utf-8")
                doOutput = true
                requestMethod = "GET"
            }
            val response = String(connection.inputStream.readAllBytes(), Charsets.UTF_8)

            saveFileIOS(config, response, language)
        }
    }

    private fun saveFileAndroid(config: LocaliseConfig, text: String, appendix: String = "") {
        val directory = File("${config.resDir}/$appendix")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory.absolutePath + "/" + config.fileName + ".xml")
        file.writeText(text)
    }

    private fun saveFileIOS(config: LocaliseConfig, text: String, appendix: String = "") {
        val directory = File("${config.resDir}/$appendix.lproj/")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory.absolutePath + "/" + config.fileName + ".strings")
        file.writeText(text)
    }

    companion object {
        internal const val NAME = "syncLocalise"
    }
}