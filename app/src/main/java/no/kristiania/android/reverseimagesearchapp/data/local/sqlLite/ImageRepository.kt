package no.kristiania.android.reverseimagesearchapp.data.local.sqlLite

import android.content.ContentValues
import no.kristiania.android.reverseimagesearchapp.core.util.bitmapToByteArray
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import javax.inject.Inject

class ImageRepository @Inject constructor(
    val database: ImageDatabaseHelper
) {

    suspend fun insertUploadedImage(image: UploadedImage){
        val db = database.writableDatabase
        val byteArray = bitmapToByteArray(image.bitmap)
        db.insert("uploaded_images", null, ContentValues().apply {
            put("title", image.title)
            put("image", byteArray)
        })
    }

}