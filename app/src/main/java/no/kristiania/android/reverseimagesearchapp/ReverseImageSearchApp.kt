package no.kristiania.android.reverseimagesearchapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ReverseImageSearchApp: Application(){
//    override fun onCreate() {
//        super.onCreate()
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                "download_channel",
//                "File download",
//                NotificationManager.IMPORTANCE_HIGH
//            )
//            val notificationManager = getSystemService(NotificationManager::class.java)
//        }
//    }
}