package me.bookk.designsystem.test

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.coroutine.DispatcherProvider

class ViewModelTestDispatchers(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) {
    private var originalMain: CoroutineDispatcher? = null
    private var originalIo: CoroutineDispatcher? = null
    private var originalDefault: CoroutineDispatcher? = null

    fun install() {
        originalMain = DispatcherProvider.main
        originalIo = DispatcherProvider.io
        originalDefault = DispatcherProvider.default
        Dispatchers.setMain(dispatcher)
        DispatcherProvider.swapMain(dispatcher)
        DispatcherProvider.swapIo(dispatcher)
        DispatcherProvider.swapDefault(dispatcher)
    }

    fun uninstall() {
        originalMain?.let(DispatcherProvider::swapMain)
        originalIo?.let(DispatcherProvider::swapIo)
        originalDefault?.let(DispatcherProvider::swapDefault)
        Dispatchers.resetMain()
    }
}
