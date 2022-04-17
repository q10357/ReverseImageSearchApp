package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetUploadedImageUrl
import no.kristiania.android.reverseimagesearchapp.presentation.service.ProgressBarService
import java.io.File
import java.lang.NumberFormatException
import javax.inject.Inject

private const val TAG = "CoroutineTAG"
private const val TAGProgress = "Process"

@HiltViewModel
class UploadImageViewModel @Inject constructor(
    private val getUploadedImageUrl: GetUploadedImageUrl,
    private val dao: ImageDao
) : ViewModel() {

    var uploadedImage = MutableLiveData<UploadedImage?>()
    var mBinder = MutableLiveData<ProgressBarService.LocalBinder?>()
    private var isLoading: Boolean = false
    private var uploadeImageJob: Job? = null
    private var bitmapScaling = 2
    private var scaleFactor = 1

    private val serviceConnection = object: ServiceConnection{
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder?) {
            Log.d(TAG, "onServiceConnected: connected to service")
            val binder: ProgressBarService.LocalBinder = iBinder as ProgressBarService.LocalBinder
            mBinder.postValue(binder)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBinder.value = null
        }
    }

    //We return the ID of the selected image when inserted in our SQLLite database
    private fun addUploadedImage(image: UploadedImage): Long {
        return dao.insertUploadedImage(image)
    }

    fun onUpload(image: UploadedImage, file: File) {
        //In case job is not cancelled
        val body = getMultiPartBody(file, ProgressBarService())
//            val uploadedImageJob = async { getImageData(body) }
//            uploadedImageJob.await()
        getUploadedImageUrl(body).onEach { result ->
            when(result.status) {
                Status.SUCCESS -> {
                    Log.i(TAG, "SUCCESS")
                    Log.i(TAG, "This is retrieved Url: ${result.data}")
                    image.urlOnServer = result.data.toString()
                    uploadedImage.postValue(image)
                    addUploadedImage(image)
                    //Retrieving list of results
                    //response.data?.let { getImageData(it) }
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
}