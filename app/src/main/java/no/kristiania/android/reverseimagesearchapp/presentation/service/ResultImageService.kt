package no.kristiania.android.reverseimagesearchapp.presentation.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetReverseImageSearchItemData
import javax.inject.Inject

private const val TAG = "ResultImageDataFetchr"

@AndroidEntryPoint
class ResultImageService: Service() {
    private val binder = LocalBinder()
    private val _resultItems = MutableLiveData<List<ReverseImageSearchItem>>()
    val resultItems: LiveData<List<ReverseImageSearchItem>> = _resultItems

    @Inject
    lateinit var getReverseImageSearchItemData: GetReverseImageSearchItemData

    suspend fun fetchImageData(url: String) {
        _resultItems.value = emptyList()
        val result = getReverseImageSearchItemData(url)
        if(result.status == Status.SUCCESS){
            saveResponse(result.data as MutableList<ReverseImageSearchItem>)
        }
    }

    private fun saveResponse(response: MutableList<ReverseImageSearchItem>) {
        _resultItems.value = response
    }

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