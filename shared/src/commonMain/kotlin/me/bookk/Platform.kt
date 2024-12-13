package me.bookk

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform