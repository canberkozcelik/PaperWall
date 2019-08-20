package com.canberkozcelik.paperwall.app

import android.app.Application
import com.canberkozcelik.paperwall.BuildConfig
import timber.log.Timber

/**
 * Created by canberkozcelik on 24.03.2019.
 */
class PaperwallApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}