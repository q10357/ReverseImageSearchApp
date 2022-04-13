package no.kristiania.android.reverseimagesearchapp.data.local.sqlLite

import android.content.ContentValues
import android.util.Log
import no.kristiania.android.reverseimagesearchapp.core.util.bitmapToByteArray
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import javax.inject.Inject

private const val TAG = "ImageDAO"

class ImageRepositoryDao @Inject constructor(
    private val database: ImageDatabaseHelper
) {

    fun insertUploadedImage(image: UploadedImage){
        Log.i(TAG, "LOOOL IS IT WORKING!?!?!?")
        val db = database.writableDatabase
        val byteArray = bitmapToByteArray(image.bitmap)

        val newRowId: Long = db.insert("uploaded_images", null, ContentValues().apply {
            put("title", image.title)
            put("image", byteArray)
        })

        Log.i(TAG, "This is id: $newRowId")
    }

}