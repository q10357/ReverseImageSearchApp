package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import no.kristiania.android.reverseimagesearchapp.core.util.Resource
import no.kristiania.android.reverseimagesearchapp.core.util.Status
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetUploadedImageUrl
import no.kristiania.android.reverseimagesearchapp.presentation.UploadedImageState
import okhttp3.MultipartBody
import javax.inject.Inject

private const val TAG = "CoroutineTAG"

@HiltViewModel
class UploadImageViewModel @Inject constructor(
    private val getUploadedImageUrl: GetUploadedImageUrl
) : ViewModel() {

    private var isLoading = UploadedImageState().isLoading
    private var uploadedImageUrl = UploadedImageState().uploadedImageUrl
    private var uploadeImageJob: Job? = null
    private lateinit var response: Resource<String>


    fun onUpload(body: MultipartBody.Part) {
        //In case job is not cancelled
        uploadeImageJob?.cancel()
        GlobalScope.launch(Dispatchers.IO) {

            isLoading = true
            Log.i(TAG, "Loading...")

            //To get to display our nice progressbar <3
            delay(500)
            val uploadedImageJob = async { response = getUploadedImageUrl(body) }
            uploadedImageJob.await()

            when(response.status){
                Status.SUCCESS -> {
                    Log.i(TAG, "SUCSESS")
                    Log.i(TAG, "This is retrieved Url: ${response.data}")
                    uploadedImageUrl = response.data
                    isLoading = false
                }
                Status.ERROR -> {
                    Log.i(TAG, "ERROR")
                    uploadedImageUrl = null
                    isLoading = false
                }
            }
            Log.i(TAG, "this is response ${uploadedImageUrl}")
        }
    }
}