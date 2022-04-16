package no.kristiania.android.reverseimagesearchapp.data.local

import android.content.ContentValues
import no.kristiania.android.reverseimagesearchapp.core.util.bitmapToByteArray
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.data.local.sqlLite.ImageDatabaseHelper
import javax.inject.Inject

private const val TAG = "ImageDAO"

class ImageDao @Inject constructor(
    private val database: ImageDatabaseHelper
) {

    //We want to know the ID of the uploaded picture, so we return the newRowId
    fun insertUploadedImage(image: UploadedImage): Long {
        val db = database.writableDatabase
        val byteArray = bitmapToByteArray(image.bitmap)

        val newRowId = db.insert("uploaded_images", null, ContentValues().apply {
            put("title", image.title)
            put("image", byteArray)
        })

        return newRowId
    }
}