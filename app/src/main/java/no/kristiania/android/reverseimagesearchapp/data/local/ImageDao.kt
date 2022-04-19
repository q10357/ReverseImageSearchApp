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



        val newRowId = db.insert("uploaded_images", null, ContentValues().apply {
            put("title", image.title)
            put("parent_id", image.parentId)
            put("image", convertUploadedImageToByteArray(image))

        })

        return newRowId
    }
    private fun convertUploadedImageToByteArray(image: UploadedImage): ByteArray? {
        return image.bitmap?.let { bitmapToByteArray(it) }
    }

    //inserting to the database for collection of searches
    fun insertSavedResult(uploaded: UploadedImage): Long{

        val db = database.writableDatabase

        //parent-id, blob, id
        val image = convertUploadedImageToByteArray(uploaded)
        val newResult = db.insert("result_images", null, ContentValues().apply {
            put("parent_id",uploaded.parentId)
            //put("image_size", list.size)
            put("image", image)

        })
        return newResult
    }

    //todo delete and save
}