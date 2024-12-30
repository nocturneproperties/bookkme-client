package me.bookk.android

import me.bookk.di.StateFactoryCreator
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.factory.AndroidAuthStateFactory

class AndroidStateFactoryCreator : StateFactoryCreator {
    override fun createAuthFactory(): AuthStateFactory {
        return AndroidAuthStateFactory()
    }
}