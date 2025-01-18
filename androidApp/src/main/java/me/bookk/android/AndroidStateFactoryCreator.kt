package me.bookk.android

import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.factory.AndroidAuthStateFactory
import me.bookk.presentation.StateFactoryCreator

class AndroidStateFactoryCreator : StateFactoryCreator {
    override fun createAuthFactory(): AuthStateFactory {
        return AndroidAuthStateFactory()
    }
}