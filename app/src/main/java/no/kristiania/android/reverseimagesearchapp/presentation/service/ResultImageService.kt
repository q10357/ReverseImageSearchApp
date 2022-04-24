package no.kristiania.android.reverseimagesearchapp.presentation.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.presentation.model.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetReverseImageSearchItemData
import no.kristiania.android.reverseimagesearchapp.presentation.PopupView
import java.util.*
import javax.inject.Inject

private const val TAG = "ResultImageDataFetchr"

@AndroidEntryPoint
class ResultImageService: Service() {
    private val binder = LocalBinder()
    private val _resultItems = MutableLiveData<List<ReverseImageSearchItem>>()
    val resultItems: LiveData<List<ReverseImageSearchItem>> = _resultItems
    val mResult = MutableLiveData<Resource<String>>()

    @Inject
    lateinit var getReverseImageSearchItemData: GetReverseImageSearchItemData

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val url = intent?.getStringExtra("image_url")

        if(url != null){
            GlobalScope.launch {
                fetchImageData(url)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private suspend fun fetchImageData(url: String) {
        val result = getReverseImageSearchItemData(url)

        if(result.status == Status.SUCCESS){
            saveResponse(result.data as MutableList<ReverseImageSearchItem>)
        }else if(result.status == Status.ERROR){
            mResult.postValue(result.message?.let {
                Resource.error(
                    message = it
                )
            })
        }
    }

    private fun saveResponse(response: MutableList<ReverseImageSearchItem>) {
        _resultItems.postValue(response)
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
        Log.i(TAG, "Removing task")
        stopSelf()
    }
}