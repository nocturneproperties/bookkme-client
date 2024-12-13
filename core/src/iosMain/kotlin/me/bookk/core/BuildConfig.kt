package me.bookk.core

import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual object AppBuildConfig {
    actual val isDebug: Boolean = Platform.isDebugBinary
    actual val flavour: ProductFlavour = ProductFlavour.DEV
}