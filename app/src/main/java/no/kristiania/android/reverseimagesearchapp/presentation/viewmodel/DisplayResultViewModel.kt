package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.presentation.service.ResultImageService
import java.util.concurrent.Executors
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

private const val TAG = "DisplayResultImages"

@HiltViewModel
class DisplayResultViewModel @Inject constructor(
    private val dao: ImageDao,
) : ViewModel() {
    private val mBinder = MutableLiveData<ResultImageService.LocalBinder?>()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.i(TAG, "Connected to the service")
            val binder = service as ResultImageService.LocalBinder
            mBinder.postValue(binder)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.i(TAG, "Disconnected from service")
            mBinder.postValue(null)
        }
    }

    fun getConnection(): ServiceConnection {
        return connection
    }

    fun getBinder(): LiveData<ResultImageService.LocalBinder?> {
        return mBinder
    }

    suspend fun saveParentImage(image: UploadedImage): Long {
        return dao.insertUploadedImage(image)
    }

    suspend fun saveChildImage(image: ReverseImageSearchItem) {
        dao.insertResultImages(image)
    }
}