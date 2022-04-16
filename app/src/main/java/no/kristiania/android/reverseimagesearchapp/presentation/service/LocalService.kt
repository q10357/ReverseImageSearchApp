package no.kristiania.android.reverseimagesearchapp.presentation.service

import android.app.Service
import android.content.Intent
import android.net.LocalServerSocket
import android.os.Binder
import android.os.IBinder

class LocalService: Service() {

    private val binder = LocalBinder()

    inner class LocalBinder: Binder() {
        //Returning an instance og localservice
        fun getService(): LocalService = this@LocalService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
}