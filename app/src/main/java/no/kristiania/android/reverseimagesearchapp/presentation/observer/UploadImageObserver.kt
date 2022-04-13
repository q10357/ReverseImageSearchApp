package no.kristiania.android.reverseimagesearchapp.presentation.observer

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import no.kristiania.android.reverseimagesearchapp.core.util.uriToBitmap
import no.kristiania.android.reverseimagesearchapp.core.util.wasInit

private const val TAG = "MainActivityTAG"

class UploadImageObserver(
    private val registry: ActivityResultRegistry,
    private val context: Context,
) : DefaultLifecycleObserver {
    lateinit var getContent: ActivityResultLauncher<String>
    var bitmap = MutableLiveData<Bitmap>()

    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register("key", owner, GetContent()) {
            if (it != null) {
                val photoUri = it
                bitmap.value = uriToBitmap(context, photoUri)
            }
        }
    }

    fun selectImage(){
        getContent.launch("image/*")
    }
}