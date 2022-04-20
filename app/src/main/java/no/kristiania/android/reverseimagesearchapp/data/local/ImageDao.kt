package no.kristiania.android.reverseimagesearchapp.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.BaseColumns
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import dagger.hilt.android.qualifiers.ApplicationContext
import no.kristiania.android.reverseimagesearchapp.core.util.bitmapToByteArray
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage
import no.kristiania.android.reverseimagesearchapp.data.remote.dto.ResultImageDto
import no.kristiania.android.reverseimagesearchapp.data.remote.dto.toReverseImageSearchItem
import java.io.File
import java.sql.Blob
import javax.inject.Inject

private const val TAG = "ImageDAO"

class ImageDao @Inject constructor(
    private val context: Context,
    private val database: ImageDatabaseHelper
) {

    private val cacheDir = context.applicationContext.cacheDir

    //We want to know the ID of the uploaded picture, so we return the newRowId
    fun insertUploadedImage(image: UploadedImage): Long {
        val db = database.writableDatabase
        val file = File(cacheDir, image.photoFileName)
        val bitmap: Bitmap = BitmapFactory.decodeFile(file.path)

        val byteArray = bitmapToByteArray(bitmap)

        val newRowId = db.insert("uploaded_images", null, ContentValues().apply {
            put("title", image.title)
            put("image", byteArray)
        })

        return newRowId
    }


     fun insertResultImages(image: ReverseImageSearchItem): Long{

        val db = database.writableDatabase
        val byteArray = image.bitmap?.let { bitmapToByteArray(it) }
        val newResult = db.insert("result_images", null, ContentValues().apply {
            put("image",byteArray )
            put("parent_id", image.parentImageId)
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