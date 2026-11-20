package me.bookk.core.test

internal actual fun prepareTestEnvironment() {
    js(
        """
        if (typeof require !== 'undefined' && require.extensions) {
            ['.json', '.svg', '.png'].forEach(function (extension) {
                require.extensions[extension] = function (module, filename) { module.exports = filename; };
            });
        }
        """,
    )
}
