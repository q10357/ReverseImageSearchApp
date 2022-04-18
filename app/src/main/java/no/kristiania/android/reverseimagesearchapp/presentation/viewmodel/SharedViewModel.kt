package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import no.kristiania.android.reverseimagesearchapp.core.util.Status
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetReverseImageSearchItemData
import no.kristiania.android.reverseimagesearchapp.presentation.service.ResultImageService
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val getReverseImageSearchItemData: GetReverseImageSearchItemData
): ViewModel() {
    private val mBinder = MutableLiveData<ResultImageService.LocalBinder?>()
    private val _resultItems = MutableLiveData<List<ReverseImageSearchItem?>>()
    val resultItems: LiveData<List<ReverseImageSearchItem?>> = _resultItems

    suspend fun fetchImageData(url: String): List<ReverseImageSearchItem>? {
        _resultItems.value = mutableListOf()
        val result = getReverseImageSearchItemData(url)
        if(result.status == Status.SUCCESS){
            saveResponse(result.data as MutableList<ReverseImageSearchItem>)
            return result.data
        }
        return null
    }

    fun saveResponse(response: MutableList<ReverseImageSearchItem>) {
        _resultItems.value = response
    }

}