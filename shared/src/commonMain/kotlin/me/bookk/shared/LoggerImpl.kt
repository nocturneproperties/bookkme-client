package me.bookk.shared

import io.ktor.util.logging.KtorSimpleLogger
import me.bookk.core.Logger

internal class LoggerImpl(name: String) : Logger {
    private val logger = KtorSimpleLogger(name)

    override fun d(message: String) {
        logger.debug(message)
    }

    override fun e(throwable: Throwable) {
        logger.error("No message", throwable)
    }

    override fun e(message: String, throwable: Throwable) {
        logger.error("No message", throwable)
    }

    override fun i(message: String) {
        logger.info(message)
    }
}