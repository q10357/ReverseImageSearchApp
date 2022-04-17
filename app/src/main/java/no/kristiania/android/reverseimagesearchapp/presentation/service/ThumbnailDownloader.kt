package no.kristiania.android.reverseimagesearchapp.presentation.service

import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ResultImageDataFetchr"

class ThumbnailDownloader<in T>(
    val service: ResultImageService,
    val onThumbnailDownloaded: (T, Bitmap) -> Unit
)
{
    fun queueThumbnail(target: T, url: String){
        val bitmap = service.fetchPhoto(url) ?: return
        onThumbnailDownloaded(target, bitmap)
    }
}