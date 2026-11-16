package me.bookk.core.test

internal actual fun prepareTestEnvironment() {
    js(
        """
        if (typeof require !== 'undefined' && require.extensions) {
            require.extensions['.json'] = function (module, filename) { module.exports = filename; };
        }
        """,
    )
}
