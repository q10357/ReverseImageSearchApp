package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private var _mResult = MutableLiveData<Resource<String>>()
    var mProgress = MutableLiveData(0)
    var mResult: LiveData<Resource<String>> = _mResult
    //We return the ID of the selected image when inserted in our SQLLite database

    fun onUpload(image: UploadedImage, file: File) {
        val body = getMultiPartBody(file, this)

        getUploadedImageUrl(body).onEach { result ->
            when (result.status) {
                Status.SUCCESS -> {
                    Log.i(TAG, "SUCCESS")
                    onFinish()
                }
                Status.ERROR -> {
                    Log.e(TAG, "ERROR")
                    onError()
                    if (isCode13(result.data)) {
                        var bitmap = fileToBitmap(file)
                        bitmap = getScaledBitmap(bitmap, bitmapScaling * scaleFactor)
                        scaleFactor++
                        createFileFromBitmap(bitmap, file)
                        onUpload(image, file)
                    }
                }
                Status.LOADING -> {
                    Log.i(TAG, "Loading...")
                }
            }
            _mResult.postValue(result)
        }.launchIn(viewModelScope)
    }

    //If the code is 413, we know the image is too large,
    //If this is the case, we will scale the bitmap, and increase the scalingFactor,
    //If the image still is too large, it will be scaled down until "infinity"
    private fun isCode13(data: String?): Boolean {
        data ?: return false
        val code: Int
        try {
            code = data.toInt()
        } catch (e: NumberFormatException) {
            return false
        }
        if (code == 413) {
            Log.i(TAG, "Photo to big, resize instantiated")
            return true
        }
        return false
    }

    override fun onProgressUpdate(percentage: Int) {
        mProgress.value = percentage
    }

    override fun onError() {
        mProgress.postValue(0)
        Log.e(TAG, "Error in upload")
    }

    override fun onFinish() {
        mProgress.postValue(0)
        Log.i(TAG, "Upload finish")
    }
}