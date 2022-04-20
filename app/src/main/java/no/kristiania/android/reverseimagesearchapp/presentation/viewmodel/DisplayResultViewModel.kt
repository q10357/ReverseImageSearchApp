package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

private const val TAG = "DisplayResultImages"

@HiltViewModel
class DisplayResultViewModel @Inject constructor(
    private val dao: ImageDao,
) : ViewModel() {

    suspend fun saveParentImage(image: UploadedImage): Long = withContext(IO) {
        return@withContext dao.insertUploadedImage(image)
    }

    suspend fun saveChildImage(image: ReverseImageSearchItem) {
        dao.insertResultImages(image)
    }
}