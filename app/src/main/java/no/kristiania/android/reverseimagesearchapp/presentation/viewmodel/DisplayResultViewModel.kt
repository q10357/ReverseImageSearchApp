package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.presentation.model.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.presentation.model.UploadedImage
import javax.inject.Inject

private const val TAG = "DisplayResultImages"

@HiltViewModel
class DisplayResultViewModel @Inject constructor(
    private val dao: ImageDao
) : ViewModel() {

    suspend fun saveParentImage(image: UploadedImage): Long {
        return dao.insertUploadedImage(image)
    }

    suspend fun saveChildImage(image: ReverseImageSearchItem) {
            dao.insertResultImages(image)
    }
}