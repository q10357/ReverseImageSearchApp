package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import no.kristiania.android.reverseimagesearchapp.core.util.Status
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetReverseImageSearchItemData
import javax.inject.Inject

private const val TAG = "DisplayResultImages"

@HiltViewModel
class DisplayResultViewModel@Inject constructor(
    private val getReverseImageSearchItemData: GetReverseImageSearchItemData,
    private val dao: ImageDao
)  : ViewModel() {
    private var isLoading = false
    var listResults: List<ReverseImageSearchItem>? = null

    fun fetchSearchResults(image: UploadedImage){
        isLoading = true
        getReverseImageSearchItemData(image.urlOnServer!!).onEach { result ->
            when(result.status) {
                Status.SUCCESS -> {
                    Log.i(TAG, "SUCCESS")
                    listResults = result.data
                    Log.i(TAG, "This is retrieved list: ${result.data}")
                    isLoading = false
                }
                Status.ERROR -> {
                    Log.i(TAG, "ERROR")
                    isLoading = false
                }
                Status.LOADING -> {
                    isLoading = true
                    Log.i(TAG, "Loading...")
                }
            }
        }.launchIn(GlobalScope)
    }
}