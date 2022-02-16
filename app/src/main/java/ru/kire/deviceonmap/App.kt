package ru.kire.deviceonmap

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ru.kire.deviceonmap.di.Modules

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                Modules.application,
                Modules.appActivity,
                Modules.mapWindow,
                Modules.markersWindow,
            )
        }
    }
}