package no.kristiania.android.reverseimagesearchapp.presentation.service

import android.graphics.Bitmap
import android.os.Handler

private const val TAG = "ResultImageDataFetchr"

class ThumbnailDownloader<in T>(
    private val responseHandler: Handler,
    private val service: ResultImageService,
    val onThumbnailDownloaded: (T, Bitmap) -> Unit
)
{


//    fun queueThumbnail(target: T, url: String){
//        val bitmap = service.fetchPhoto(url) ?: return
//        onThumbnailDownloaded(target, bitmap)
//    }
}