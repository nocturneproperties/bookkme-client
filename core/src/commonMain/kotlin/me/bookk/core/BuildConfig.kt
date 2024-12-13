package me.bookk.core

expect object AppBuildConfig {
    val isDebug: Boolean
    val flavour: ProductFlavour
}