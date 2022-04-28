package no.kristiania.android.reverseimagesearchapp.presentation.observer

import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "MainActivityTAG"

class RegisterActivityResultsObserver(
    private val registry: ActivityResultRegistry,
) : DefaultLifecycleObserver {
    lateinit var getContent: ActivityResultLauncher<String>
    var uri = MutableLiveData<Uri?>()

    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register("key", owner, GetContent()) {
            if (it != null) {
                uri.value = it
            }
        }
    }

    suspend fun selectImage(){
        getContent.launch("image/*")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        uri.value = null
        Log.i(TAG, "Uri value is: ${uri.value}")
        super.onDestroy(owner)
    }
}