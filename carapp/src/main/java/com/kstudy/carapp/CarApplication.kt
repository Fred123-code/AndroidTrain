package com.kstudy.carapp

import android.app.Application
import android.os.StrictMode
import com.kstudy.monitor.fps.ChoreographerHelper

class CarApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()      //IO流
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()           //日志
                .build())

            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()           //日志
                .penaltyDeath()
                .build())
        }

//        ChoreographerHelper.start()
    }
}