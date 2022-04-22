package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.service.ResultImageService
import javax.inject.Inject

private const val TAG = "DisplayResultImages"

@HiltViewModel
class DisplayResultViewModel @Inject constructor(
    private val dao: ImageDao,
) : ViewModel() {

    suspend fun saveParentImage(image: UploadedImage): Long {
        return dao.insertUploadedImage(image)
    }

    suspend fun saveChildImage(image: ReverseImageSearchItem) {
            dao.insertResultImages(image)
    }
}