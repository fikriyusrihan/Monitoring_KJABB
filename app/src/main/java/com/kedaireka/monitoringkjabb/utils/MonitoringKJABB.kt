package com.kedaireka.monitoringkjabb.utils

import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MonitoringKJABB : Application() {
    override fun onCreate() {
        super.onCreate()

        Firebase.database.setPersistenceEnabled(true)
    }
}