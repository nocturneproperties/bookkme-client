package me.bookk.feature.platform.domain.api

interface OpenUrlPreview {
    operator fun invoke(url: String)
}