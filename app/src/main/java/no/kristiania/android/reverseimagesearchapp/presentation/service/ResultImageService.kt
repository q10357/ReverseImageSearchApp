package no.kristiania.android.reverseimagesearchapp.presentation.service

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.*
import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.*
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.presentation.model.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetReverseImageSearchItemData
import java.util.*
import javax.inject.Inject

private const val TAG = "ResultImageDataFetchr"

@AndroidEntryPoint
class ResultImageService: Service() {
    private val binder = LocalBinder()
    private val _resultItems = MutableLiveData<List<ReverseImageSearchItem>>()
    val resultItems: LiveData<List<ReverseImageSearchItem>> = _resultItems

    private val _mResult = MutableLiveData<Resource<String>>()
    val mResult: LiveData<Resource<String>> = _mResult

    @Inject
    lateinit var getReverseImageSearchItemData: GetReverseImageSearchItemData

    fun onStart(url: String?){
        url ?: return
        _mResult.value = Resource.loading()
        GlobalScope.launch(IO) {
            fetchImageData(url)
        }
    }

    private suspend fun fetchImageData(url: String) {
        val result = getReverseImageSearchItemData(url)
        if(result.status == Status.SUCCESS){
            _mResult.postValue(Resource.success(
                data = result.data?.size.toString(),
                message = result.message.toString()
            ))
            Log.i(TAG, "The size of result: ${result.data?.size}")
            saveResponse(result.data as MutableList<ReverseImageSearchItem>)
        }else if(result.status == Status.ERROR){
            Log.i(TAG, "This is boris fucked up shitty error code ${result.data}")
            _mResult.postValue(
                Resource.error(
                    message = result.message.toString()
                )
            )
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