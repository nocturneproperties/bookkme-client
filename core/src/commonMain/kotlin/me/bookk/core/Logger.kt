package me.bookk.core

object LogFactory {

    private var logFactory: ((String) -> Logger)? = null

    fun initFactory(factory: (String) -> Logger) {
        this.logFactory = factory
    }

    fun forName(name: String): Logger {
        return logFactory?.invoke(name)
            ?: throw IllegalStateException("Platform factory is missing")
    }
}

interface Logger {
    fun d(message: String)
    fun e(throwable: Throwable)
    fun e(message: String, throwable: Throwable)
    fun i(message: String)
}