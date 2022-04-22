package no.kristiania.android.reverseimagesearchapp.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import no.kristiania.android.reverseimagesearchapp.data.local.FeedReaderContract.ResultImageTable
import android.provider.BaseColumns
import no.kristiania.android.reverseimagesearchapp.core.util.bitmapToByteArray
import no.kristiania.android.reverseimagesearchapp.data.local.FeedReaderContract.UploadedImageTable
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ParentImage
import no.kristiania.android.reverseimagesearchapp.presentation.model.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.presentation.model.UploadedImage
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
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

    //delete for delete button you press on result page
    fun deleteResult(id: Int): Int {
        val db = database.writableDatabase
        val where = "_id =${id};"
        val query = db.delete(ResultImageTable.TABLE_NAME, where, null);

        return query
    }

    fun getAllParentImages(): List<ParentImage>{
        var parentImages = mutableListOf<ParentImage>()
        val db = database.writableDatabase

        val cursor: Cursor = db.query(UploadedImageTable.TABLE_NAME, arrayOf(
            BaseColumns._ID,
            UploadedImageTable.COLUMN_NAME_TITLE,
            UploadedImageTable.COLUMN_NAME_IMAGE,
            UploadedImageTable.COLUMN_NAME_DATE
        ), null, null, null, null, "${UploadedImageTable.COLUMN_NAME_DATE} ASC" )

        while(cursor.moveToNext()){
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(
                BaseColumns._ID
            ))
            val title = cursor.getString(cursor.getColumnIndexOrThrow(
                UploadedImageTable.COLUMN_NAME_TITLE
            ))
            val image = cursor.getBlob(cursor.getColumnIndexOrThrow(
                UploadedImageTable.COLUMN_NAME_IMAGE
            ))
            val dateLongValue = cursor.getLong(cursor.getColumnIndexOrThrow(
                UploadedImageTable.COLUMN_NAME_DATE
            ))
            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
            val date: Date = Date(TimeUnit.SECONDS.toMillis(dateLongValue * 1000))

            val parentImage: ParentImage = ParentImage(
                id,
                title,
                bitmap,
                date
            )
            parentImages.add(parentImage)
        }
        return parentImages
    }

    fun getParentsChildImages(id: Long){

    }

//    fun getByResultItem(number: Int): List<ReverseImageSearchItem>{
//        val db = database.readableDatabase
//        val query = "SELECT * FROM " + FeedReaderContract.ResultImageTable.TABLE_NAME;
//        var selection = "${FeedReaderContract.ResultImageTable.ID} = $number"
//        val cursor = db.query(FeedReaderContract.ResultImageTable.TABLE_NAME,null,selection,null,null,null,null)
//
//        val itemReverse = mutableListOf<ReverseImageSearchItem>()
//        with(cursor){
//            while(moveToNext()){
//
//                val blob = getBlob(getColumnIndexOrThrow(FeedReaderContract.ResultImageTable.COLUMN_NAME_IMAGE))
//
//            }
//        }
//        return listOf()
//    }
}