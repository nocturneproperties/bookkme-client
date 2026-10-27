package me.bookk.core

interface Logger {
    fun d(message: String)
    fun e(throwable: Throwable)
    fun e(message: String, throwable: Throwable)
    fun i(message: String)

    companion object {

        private var logFactory: ((String) -> Logger)? = null

        //Intentionally not thread safe since it's only called once on the main thread
        fun initFactory(factory: (String) -> Logger) {
            this.logFactory = factory
        }

        fun create(name: String): Logger {
            return logFactory?.invoke(name) ?: emptyLogger
        }
    }
}

private val emptyLogger: Logger = object : Logger {
    override fun d(message: String) {
    }

    override fun e(throwable: Throwable) {
    }

    override fun e(message: String, throwable: Throwable) {
    }

    override fun i(message: String) {
    }

}