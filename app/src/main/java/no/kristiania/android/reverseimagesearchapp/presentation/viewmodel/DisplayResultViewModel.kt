package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.presentation.service.ResultImageService
import javax.inject.Inject

private const val TAG = "DisplayResultImages"

@HiltViewModel
class DisplayResultViewModel @Inject constructor(
    private val dao: ImageDao
)  : ViewModel() {

    fun savePhotos(items: List<ReverseImageSearchItem>){
        for (i in items){
            dao.insertResultImages(i)
        }
    }
}