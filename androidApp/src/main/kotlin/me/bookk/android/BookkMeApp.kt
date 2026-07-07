package me.bookk.android

import android.app.Application
import android.os.StrictMode
import me.bookk.di.initDI
import me.bookk.feature.settings.presentation.notifications.service.NotificationChannels
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class BookkMeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .build()
        )
        NotificationChannels.createDefaultChannel(this)
        initDI(AndroidStateFactoryCreator()) {
            androidLogger()
            androidContext(this@BookkMeApp)
        }
    }
}