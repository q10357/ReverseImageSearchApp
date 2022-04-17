package no.kristiania.android.reverseimagesearchapp.presentation.viewmodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import no.kristiania.android.reverseimagesearchapp.presentation.service.ResultImageService

class MainViewModel: ViewModel() {
    private val mBinder = MutableLiveData<ResultImageService.LocalBinder?>()
    private var mIsFetching = MutableLiveData<Boolean>()

    private val connection = object: ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            val binder = service as ResultImageService.LocalBinder
            mBinder.postValue(binder)
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            mBinder.postValue(null)
        }
    }

    fun isFetching(): LiveData<Boolean> {
        return mIsFetching
    }

    fun setIsFetching(b: Boolean) {
        mIsFetching.postValue(b)
    }

    fun getBinder(): LiveData<ResultImageService.LocalBinder?> {
        return mBinder
    }

    fun getConnection(): ServiceConnection{
        return connection
    }

}