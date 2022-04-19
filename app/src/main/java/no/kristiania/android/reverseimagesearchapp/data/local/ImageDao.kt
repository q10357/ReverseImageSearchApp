package no.kristiania.android.reverseimagesearchapp.data.local

import android.content.ContentValues
import android.provider.BaseColumns
import no.kristiania.android.reverseimagesearchapp.core.util.bitmapToByteArray
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.data.remote.dto.ResultImageDto
import no.kristiania.android.reverseimagesearchapp.data.remote.dto.toReverseImageSearchItem
import java.sql.Blob
import javax.inject.Inject

private const val TAG = "ImageDAO"

class ImageDao @Inject constructor(
    private val database: ImageDatabaseHelper
) {

    //We want to know the ID of the uploaded picture, so we return the newRowId
    fun insertUploadedImage(image: UploadedImage): Long {
        insertResultImages(image)
        val db = database.writableDatabase
        val byteArray = image.bitmap?.let { bitmapToByteArray(it) }



        val newRowId = db.insert("uploaded_images", null, ContentValues().apply {
            put("title", image.title)
            put("image", byteArray)
        })

        return newRowId
    }

    fun insertResultImages(image: UploadedImage): Long{

        val db = database.writableDatabase
        val byteArray = image.bitmap?.let { bitmapToByteArray(it) }
        val newResult = db.insert("result_images", null, ContentValues().apply {

            put("image",byteArray )
        })
        return newResult
    }

    /*
    fun getByResultItem(number: Int): List<ReverseImageSearchItem>{
        val db = database.readableDatabase
        val query = "SELECT * FROM " + FeedReaderContract.ResultImageTable.TABLE_NAME;
       // var selection = "${FeedReaderContract.ResultImageTable.ID} = $number"

       // val cursor = db.query(FeedReaderContract.ResultImageTable.TABLE_NAME,null,selection,null,null,null,null)

        val itemReverse = mutableListOf<ReverseImageSearchItem>()
        with(cursor){
            while(moveToNext()){

                val blob = getBlob(getColumnIndexOrThrow(FeedReaderContract.ResultImageTable.COLUMN_NAME_IMAGE))

            }
        }
        return listOf()
    }

     */
}