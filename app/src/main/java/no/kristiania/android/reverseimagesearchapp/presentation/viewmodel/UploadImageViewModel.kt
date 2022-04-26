package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.presentation.model.UploadedImage
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetUploadedImageUrl
import java.io.File
import javax.inject.Inject

private const val TAG = "CoroutineTAG"

@HiltViewModel
class UploadImageViewModel @Inject constructor(
    private val getUploadedImageUrl: GetUploadedImageUrl
) : ViewModel(), ProgressRequestBody.UploadCallback {
    private var bitmapScaling = 2
    private var scaleFactor = 1
    private val _mResult = MutableLiveData<Resource<String>>()
    val mResult: LiveData<Resource<String>> = _mResult
    var mProgress = MutableLiveData(0)

    fun onUpload(image: UploadedImage, file: File) {
        val body = getMultiPartBody(file, this)
        _mResult.value = Resource.loading()

        viewModelScope.launch {
            val result = async{getUploadedImageUrl(body)}
            _mResult.postValue(result.await())
        }
    }

    override fun onProgressUpdate(percentage: Int) {
        mProgress.value = percentage
    }

    override fun onError() {
        mProgress.value = 0
        Log.e(TAG, "Error in upload")
    }

    override fun onFinish() {
        mProgress.value = 0
        Log.i(TAG, "Upload finish")
    }
}