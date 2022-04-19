package no.kristiania.android.reverseimagesearchapp.data.local

import android.content.ContentValues
import no.kristiania.android.reverseimagesearchapp.core.util.bitmapToByteArray
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import javax.inject.Inject

private const val TAG = "ImageDAO"

class ImageDao @Inject constructor(
    private val database: ImageDatabaseHelper
) {

    //We want to know the ID of the uploaded picture, so we return the newRowId
    fun insertUploadedImage(image: UploadedImage): Long {
        val db = database.writableDatabase
        val byteArray = image.bitmap?.let { bitmapToByteArray(it) }

        val newRowId = db.insert("uploaded_images", null, ContentValues().apply {
            put("title", image.title)
            put("image", byteArray)

        })

        return newRowId
    }
    private fun convertUploadedImageToByteArray(image: UploadedImage): ByteArray? {
        return image.bitmap?.let { bitmapToByteArray(it) }
    }

    //inserting to the database for collection of searches
    fun insertSavedResult(uploaded: UploadedImage): Long{

        val db = database.writableDatabase
        val byteArray = uploaded.bitmap?.let { bitmapToByteArray(it) }
        //parent-id, blob, id
        val image = convertUploadedImageToByteArray(uploaded)
        val newResult = db.insert("result_images", null, ContentValues().apply {
            put("parent_id",4)
            put("image", byteArray)

        })
        return newResult
    }

    //todo delete and save
}