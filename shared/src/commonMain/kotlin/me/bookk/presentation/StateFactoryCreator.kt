package me.bookk.presentation

import me.bookk.feature.authorization.presentation.AuthStateFactory

interface StateFactoryCreator {
    fun createAuthFactory(): AuthStateFactory
}