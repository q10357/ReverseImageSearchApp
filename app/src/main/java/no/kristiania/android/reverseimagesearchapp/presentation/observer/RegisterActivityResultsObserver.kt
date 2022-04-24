package no.kristiania.android.reverseimagesearchapp.presentation.observer

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import no.kristiania.android.reverseimagesearchapp.core.util.uriToBitmap

private const val TAG = "MainActivityTAG"

class RegisterActivityResultsObserver(
    private val registry: ActivityResultRegistry,
) : DefaultLifecycleObserver {
    lateinit var getContent: ActivityResultLauncher<String>
    var uri = MutableLiveData<Uri>()

    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register("key", owner, GetContent()) {
            if (it != null) {
                uri.value = it
            }
        }
    }

    fun selectImage(){
        getContent.launch("image/*")
    }
}