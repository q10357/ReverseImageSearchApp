package no.kristiania.android.reverseimagesearchapp.presentation.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import no.kristiania.android.reverseimagesearchapp.core.util.ProgressRequestBody

private const val TAG = "LocalServiceTAG"
class ProgressBarService : Service(), ProgressRequestBody.UploadCallback{

    private val binder = LocalBinder()
    var mProgress: Int = 0

    //Provides instance of service to client
    inner class LocalBinder: Binder() {
        //Returning an instance of LocalService
        fun getService(): ProgressBarService = this@ProgressBarService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onProgressUpdate(percentage: Int) {
        Log.i(TAG, "THIS IS PROGRESS: $percentage")
    }

    override fun onError() {
        mProgress = 0
        Log.e(TAG, "Error in upload")
    }

    override fun onFinish() {
        mProgress = 0
        Log.i(TAG, "Upload finish")
    }

    //Important, service will run until client has unbound to service
    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}