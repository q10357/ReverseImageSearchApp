package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import no.kristiania.android.reverseimagesearchapp.core.util.*
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.data.local.sqlLite.ImageRepositoryDao
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetUploadedImageUrl
import no.kristiania.android.reverseimagesearchapp.presentation.UploadedImageState
import java.io.File
import java.lang.NumberFormatException
import javax.inject.Inject

private const val TAG = "CoroutineTAG"
private const val TAGProgress = "Process"

@HiltViewModel
class UploadImageViewModel @Inject constructor(
    private val getUploadedImageUrl: GetUploadedImageUrl,
    private val dao: ImageRepositoryDao
) : ViewModel(), ProgressRequestBody.UploadCallback {

    private var isLoading = UploadedImageState().isLoading
    private var uploadedImageUrl = UploadedImageState().uploadedImageUrl
    private var uploadeImageJob: Job? = null
    private var bitmapScaling = 2
    private var timeScaled = 1
    private lateinit var response: Resource<String>


    fun onUpload(image: UploadedImage, file: File) {
        //In case job is not cancelled
        val body = getMultiPartBody(file, this)

        uploadeImageJob?.cancel()
        viewModelScope.launch(Dispatchers.IO) {

            isLoading = true
            Log.i(TAG, "Loading...")

            //To get to display our nice progressbar <3
            delay(500)
            val uploadedImageJob = async { response = getUploadedImageUrl(body) }
            uploadedImageJob.await()

            when(response.status){
                Status.SUCCESS -> {
                    Log.i(TAG, "SUCCESS")
                    Log.i(TAG, "This is retrieved Url: ${response.data}")
                    uploadedImageUrl = response.data
                    dao.insertUploadedImage(image)
                    isLoading = false
                }
                Status.ERROR -> {
                    Log.i(TAG, "ERROR")
                    uploadedImageUrl = null
                    isLoading = false
                    if(isCode13(response.data)){
                        image.bitmap = getScaledBitmap(image.bitmap, bitmapScaling * timeScaled)
                        timeScaled ++
                        createFileFromBitmap(image.bitmap, file)
                        onUpload(image, file)
                    }
                }
            }
        }
    }

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
        Log.i(TAGProgress, "This is percentage $percentage")
    }

    override fun onError() {
        Log.e(TAGProgress, "Error in upload")
    }

    override fun onFinish() {
        Log.i(TAGProgress, "Upload finish")
    }

}