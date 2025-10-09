package de.malteans.sosactivities

import android.app.Application
import de.malteans.sosactivities.di.initKoin
import org.koin.android.ext.koin.androidContext

class SosActivitiesApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@SosActivitiesApplication)
        }
    }
}