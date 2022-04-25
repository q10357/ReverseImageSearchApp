package no.kristiania.android.reverseimagesearchapp.presentation.observer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import no.kristiania.android.reverseimagesearchapp.presentation.model.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.presentation.service.ResultImageService
import no.kristiania.android.reverseimagesearchapp.presentation.service.ThumbnailDownloader

private const val TAG = "DisplayResultObserver"

class DisplayResultObserver<T>(
    private val thumbnailDownloader: ThumbnailDownloader<T>,
    private val activity: FragmentActivity
): DefaultLifecycleObserver {
    private val mBinder = MutableLiveData<ResultImageService.LocalBinder?>()
    private var mService: ResultImageService? = null
    private var mBound = false
    val resultItems = MutableLiveData<List<ReverseImageSearchItem>>()
    val _resultItems: LiveData<List<ReverseImageSearchItem>> = resultItems

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        thumbnailDownloader.onCreate()
        bindService()
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            Log.i(TAG, "Connected to the service")
            val binder = service as ResultImageService.LocalBinder
            mBound = true
            mService = binder.getService()
            mBinder.postValue(binder)
            serviceInit()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            Log.i(TAG, "Disconnected from service")
            mService = null
            mBound = false
            mBinder.postValue(null)
        }
    }

    private fun serviceInit() {
        thumbnailDownloader.service = mService

        mService!!.resultItems.observe(
            this.activity
        ) {
            resultItems.value = it
            Log.i(TAG, "List size: ${resultItems.value?.size}")
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        unBindService()
        thumbnailDownloader.onDestroy()
    }

    fun onDestroyView(){
        thumbnailDownloader.onDestroyView()
    }

    private fun bindService() {
        val serviceIntent = Intent(activity, ResultImageService::class.java)
        activity.bindService(serviceIntent,
            connection,
            Context.BIND_AUTO_CREATE)
        mBound = true
    }

    private fun unBindService(){
        activity.unbindService(connection)
    }
}