package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import javax.inject.Inject

private const val TAG = "DisplayResultImages"

@HiltViewModel
class DisplayResultViewModel @Inject constructor(
    private val dao: ImageDao,
) : ViewModel() {

    fun saveParentImage(image: UploadedImage): Long {
        return dao.insertUploadedImage(image)
    }

    fun saveChildImage(image: ReverseImageSearchItem) {
        dao.insertResultImages(image)
    }
}