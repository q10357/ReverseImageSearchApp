package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetUploadedImageUrl
import java.io.File
import java.lang.NumberFormatException
import javax.inject.Inject

private const val TAG = "CoroutineTAG"
private const val TAGProgress = "Process"

@HiltViewModel
class UploadImageViewModel @Inject constructor(
    private val getUploadedImageUrl: GetUploadedImageUrl,
    private val dao: ImageDao
) : ViewModel(), ProgressRequestBody.UploadCallback {
    var uploadedImage = MutableLiveData<UploadedImage?>()
    private var isLoading: Boolean = false
    private var bitmapScaling = 2
    private var scaleFactor = 1
    val mProgress = MutableStateFlow(0)
    //We return the ID of the selected image when inserted in our SQLLite database
    private fun addUploadedImage(image: UploadedImage): Long {
        return dao.insertUploadedImage(image)
    }

    fun onUpload(image: UploadedImage, file: File) {
        val body = getMultiPartBody(file, this)

        getUploadedImageUrl(body).onEach { result ->
            when(result.status) {
                Status.SUCCESS -> {
                    Log.i(TAG, "SUCCESS")
                    Log.i(TAG, "This is retrieved Url: ${result.data}")
                    image.urlOnServer = result.data.toString()
                    uploadedImage.postValue(image)
                    addUploadedImage(image)
                    isLoading = false
                }
                Status.ERROR -> {
                    Log.i(TAG, "ERROR")
                    isLoading = false
                    if (isCode13(result.data)) {
                        image.bitmap = getScaledBitmap(image.bitmap, bitmapScaling * scaleFactor)
                        scaleFactor++
                        createFileFromBitmap(image.bitmap, file)
                        onUpload(image, file)
                    }
                }
                Status.LOADING -> {
                    isLoading = true
                    Log.i(TAG, "Loading...")
                }
            }
        }.launchIn(GlobalScope)
    }

    //If the code is 413, we know the image is too large,
    //If this is the case, we will scale the bitmap, and increase the scalingFactor,
    //If the image still is too large, it will be scaled down until "infinity"
    private fun isCode13(data: String?): Boolean {
        val isNull = data ?: return false
        var code = 0
        try {
            code = data.toInt()
        }catch (e: NumberFormatException){
            return false
        }
        if(code == 413){
            Log.i(TAG, "Photo to big, resize instantiated")
            return true
        }
        return false
    }

    override fun onProgressUpdate(percentage: Int) {
        mProgress.value = percentage
        Log.i(TAG, "THIS IS PROGRESS: $percentage")
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