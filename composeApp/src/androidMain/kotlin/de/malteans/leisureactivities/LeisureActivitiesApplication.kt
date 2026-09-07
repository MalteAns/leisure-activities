package de.malteans.leisureactivities

import android.app.Application
import de.malteans.leisureactivities.di.initKoin
import org.koin.android.ext.koin.androidContext

class LeisureActivitiesApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@LeisureActivitiesApplication)
        }
    }
}