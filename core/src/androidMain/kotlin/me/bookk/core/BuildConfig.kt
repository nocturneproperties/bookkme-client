package me.bookk.core

actual object AppBuildConfig {
    actual val isDebug: Boolean = BuildConfig.DEBUG
    actual val flavour: ProductFlavour = ProductFlavour.values().first { it.title == BuildConfig.FLAVOR }
}