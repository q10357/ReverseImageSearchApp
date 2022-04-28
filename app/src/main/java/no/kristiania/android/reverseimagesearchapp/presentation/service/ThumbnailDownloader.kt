package no.kristiania.android.reverseimagesearchapp.presentation.service

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0


class ThumbnailDownloader<in T>(
    private val responseHandler: Handler,
    var service: ResultImageService?,
    val onThumbnailDownloaded: (T, Bitmap) -> Unit,
) : HandlerThread(TAG) {

    fun onCreate() {
        Log.i(TAG, "Starting background thread")
        start()
        looper
    }

    fun onDestroy() {
        Log.i(TAG, "Destroying background thread")
        quit()
    }

    fun onDestroyView() {
        Log.i(TAG, "Clearing queue")
        requestHandler.removeMessages(MESSAGE_DOWNLOAD)
        requestMap.clear()
    }


    private lateinit var requestHandler: Handler
    private var hasQuit = false
    private val requestMap = ConcurrentHashMap<T, String>()

    override fun onLooperPrepared() {
        requestHandler = object : Handler(looper) {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    handleRequest(target)
                }
            }
        }
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        var bitmap: Bitmap?


        runBlocking {
            val networkResult = service?.fetchPhoto(url)
            bitmap = networkResult
        }

        val bmp = bitmap ?: return
        Log.i(TAG, "This is the results size: Width: ${bmp.width}")
        Log.i(TAG, "This is the results size: Height: ${bmp.height}")

        responseHandler.post(Runnable {
            if (requestMap[target] != url || hasQuit) {
                return@Runnable
            }

            requestMap.remove(target)
            onThumbnailDownloaded(target, bmp)
        })
    }

    fun queueThumbnail(target: T, url: String) {
        requestMap[target] = url
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target)
            .sendToTarget()
    }

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }
}