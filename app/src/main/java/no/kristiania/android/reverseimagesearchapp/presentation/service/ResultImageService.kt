package no.kristiania.android.reverseimagesearchapp.presentation.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetReverseImageSearchItemData
import javax.inject.Inject

private const val TAG = "ResultImageDataFetchr"

@AndroidEntryPoint
class ResultImageService: Service() {
    private val binder = LocalBinder()

    @Inject
    lateinit var getReverseImageSearchItemData: GetReverseImageSearchItemData

    //Provides instance of service to client
    inner class LocalBinder: Binder() {
        //Returning an instance of LocalService
        fun getService(): ResultImageService = this@ResultImageService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    suspend fun fetchPhoto(url: String): Bitmap? {
        return getReverseImageSearchItemData.fetchPhoto(url)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}