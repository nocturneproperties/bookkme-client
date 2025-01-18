package me.bookk.di

import me.bookk.feature.authorization.presentation.AuthStateFactory

interface StateFactoryCreator {
    fun createAuthFactory(): AuthStateFactory
}