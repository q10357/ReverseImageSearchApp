package no.kristiania.android.reverseimagesearchapp.data.local

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import no.kristiania.android.reverseimagesearchapp.data.local.FeedReaderContract.ResultImageTable
import android.provider.BaseColumns
import androidx.lifecycle.LiveData
import no.kristiania.android.reverseimagesearchapp.core.util.bitmapToByteArray
import no.kristiania.android.reverseimagesearchapp.data.local.FeedReaderContract.UploadedImageTable
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ChildImage
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ParentImage
import no.kristiania.android.reverseimagesearchapp.presentation.model.CollectionItem
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

    fun getChildImage(id: Long): ChildImage {
        val db = database.writableDatabase
        val where = "_id =${id}"
        val cursor: Cursor = db.query(ResultImageTable.TABLE_NAME, null, where, null, null, null, null)

        val result = retrieveChildImageData(cursor)
        assert(result.size == 1)
        return result[0]
    }

    fun getParentImage(id: Long): ParentImage {
        val db = database.writableDatabase
        val where = "_id =${id}"
        val cursor: Cursor = db.query(UploadedImageTable.TABLE_NAME, null, where, null, null, null, null)

        val result = retrieveParentImageData(cursor)
        assert(result.size == 1)
        return result[0]
    }

    fun deleteCollection(parentId: Long): Int {
        val db = database.writableDatabase
        val where = "_id =${parentId};"
        return db.delete(ResultImageTable.TABLE_NAME, where, null)
    }

    fun deleteAllChildren(parentId: Long): Int {
        val db = database.writableDatabase
        val where = "parent_id =${parentId};"
        val query = db.delete(ResultImageTable.TABLE_NAME, where, null);

        return query
    }

    fun deleteParent(id: Long): Int{
        val db = database.writableDatabase
        val where = "_id =${id};"
        return db.delete(UploadedImageTable.TABLE_NAME, where, null);
    }

    //delete for delete button you press on result page
    fun deleteChild(id: Int): Int {
        val db = database.writableDatabase
        val where = "_id =${id};"
        return db.delete(ResultImageTable.TABLE_NAME, where, null);
    }

    fun getAllParentImages(): List<ParentImage> {
        val db = database.writableDatabase
        val cursor: Cursor = db.query(UploadedImageTable.TABLE_NAME, null, null, null, null, null, "${UploadedImageTable.COLUMN_NAME_DATE} DESC" )

        return retrieveParentImageData(cursor)
    }

    fun getParentsChildImages(id: Long): List<ChildImage>{
        val selection = ResultImageTable.COLUMN_NAME_PARENT_ID + "= ?"
        val db = database.writableDatabase

        val cursor: Cursor = db.query(ResultImageTable.TABLE_NAME, null, selection, arrayOf(id.toString()), null, null, null )

        return retrieveChildImageData(cursor)
    }

    private fun retrieveChildImageData(cursor: Cursor): List<ChildImage> {
        val result = mutableListOf<ChildImage>()
        while(cursor.moveToNext()){
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(
                BaseColumns._ID
            ))
            val image = cursor.getBlob(cursor.getColumnIndexOrThrow(
                ResultImageTable.COLUMN_NAME_IMAGE
            ))
            val parentId = cursor.getLong(cursor.getColumnIndexOrThrow(
                ResultImageTable.COLUMN_NAME_PARENT_ID
            ))

            val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)

            val childImage: ChildImage = ChildImage(
                id = id,
                bitmap = bitmap,
                parentId = parentId
            )
            result.add(childImage)
        }
        cursor.close()
        return result
    }


    private fun retrieveParentImageData(cursor: Cursor): List<ParentImage> {
        val result = mutableListOf<ParentImage>()
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
                id = id,
                title = title,
                bitmap = bitmap,
                date = date
            )
            result.add(parentImage)
        }
        cursor.close()
        return result
    }
}