package com.example.spendee

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.spendee.workmanager.DeadlinesCheckerWorker
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class SpendeeApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Schedule the periodic worker
        val workRequest = PeriodicWorkRequestBuilder<DeadlinesCheckerWorker>(1, TimeUnit.DAYS)
            .build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
