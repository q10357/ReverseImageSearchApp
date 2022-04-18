package no.kristiania.android.reverseimagesearchapp.presentation.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetReverseImageSearchItemData
import javax.inject.Inject

private const val TAG = "ResultImageDataFetchr"

@AndroidEntryPoint
class ResultImageService: Service() {
    private val binder = LocalBinder()
    private var url: String = ""
    private var listResults = MutableStateFlow(mutableListOf<ReverseImageSearchItem>())
    var isFetching = MutableStateFlow(false)
    @Inject
    lateinit var dao: ImageDao
    @Inject
    lateinit var getReverseImageSearchItemData: GetReverseImageSearchItemData

    val getImages: List<ReverseImageSearchItem>? = listResults.value

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "OnCreate")
    }

    //Provides instance of service to client
    inner class LocalBinder: Binder() {
        //Returning an instance of LocalService
        fun getService(): ResultImageService = this@ResultImageService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    suspend fun fetchImageData(url: String): List<ReverseImageSearchItem>? {
        val result = getReverseImageSearchItemData(url)
        if(result.status == Status.SUCCESS){
            return result.data
        }
        return null
    }

    fun fetchPhoto(url: String): Bitmap? {
        return getReverseImageSearchItemData.fetchPhoto(url)
    }
}