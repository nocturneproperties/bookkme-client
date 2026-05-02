package me.bookk.shared

import io.github.aakira.napier.Napier
import me.bookk.core.Logger
import io.ktor.client.plugins.logging.Logger as KtorLogger

internal class LoggerImpl(private val name: String) : Logger, KtorLogger {

    override fun d(message: String) {
        Napier.d(message, tag = name)
    }

    override fun e(throwable: Throwable) {
        Napier.e("Failed: ", throwable, tag = name)
    }

    override fun e(message: String, throwable: Throwable) {
        Napier.e(message, throwable, tag = name)
    }

    override fun i(message: String) {
        Napier.i(message, tag = name)
    }

    override fun log(message: String) {
        i(message)
    }
}