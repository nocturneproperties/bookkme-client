package me.bookk.android

import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.factory.AndroidAuthStateFactory
import me.bookk.feature.dashboard.presentation.AndroidDashboardStateFactory
import me.bookk.feature.dashboard.presentation.DashboardStateFactory
import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.factory.AndroidSettingsStateFactory
import me.bookk.presentation.StateFactoryCreator

class AndroidStateFactoryCreator : StateFactoryCreator {
    override fun createAuthFactory(): AuthStateFactory {
        return AndroidAuthStateFactory()
    }

    override fun createDashboardFactory(): DashboardStateFactory {
        return AndroidDashboardStateFactory()
    }

    override fun createSettingsFactory(): SettingsStateFactory {
        return AndroidSettingsStateFactory()
    }
}