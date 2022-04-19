package no.kristiania.android.reverseimagesearchapp.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import no.kristiania.android.reverseimagesearchapp.core.util.provider.Constants
import no.kristiania.android.reverseimagesearchapp.data.local.FeedReaderContract.UploadedImageTable
import java.text.SimpleDateFormat
import javax.inject.Inject

class ImageDatabaseHelper @Inject constructor(context: Context): SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ${UploadedImageTable.TABLE_NAME}" +
                " (${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${UploadedImageTable.COLUMN_NAME_TITLE} TEXT, " +
                "${UploadedImageTable.COLUMN_NAME_IMAGE} BLOB)")

        db.execSQL("CREATE TABLE ${FeedReaderContract.ResultImageTable.TABLE_NAME}" +
                " (${FeedReaderContract.ResultImageTable.TABLE_ID} INTEGER PRIMARY KEY, " +
                "${FeedReaderContract.ResultImageTable.IMAGE_SIZE} integer, " +
                "${FeedReaderContract.ResultImageTable.COLUMN_NAME_IMAGE} BLOB, " +

                //foregin key conncetion
                "${FeedReaderContract.ResultImageTable.COLUMN_NAME_PARENT_ID} integer references "
                + "${UploadedImageTable.TABLE_NAME}" + "(" + "${FeedReaderContract.ResultImageTable.TABLE_ID}" + ")" +
                ")");

        //todo date

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${UploadedImageTable.TABLE_NAME}")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = Constants.DATABASE_NAME
        const val DATABASE_VERSION = 1
    }
}
object FeedReaderContract {
    object UploadedImageTable: BaseColumns {
        const val TABLE_NAME = "uploaded_images"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_IMAGE = "image"
    }

    object ResultImageTable: BaseColumns {

        const val TABLE_ID = "result_id"
        const val TABLE_NAME = "result_images"
        const val DATE = "date_created"
        const val IMAGE_SIZE = "image_size"
        const val COLUMN_NAME_IMAGE = "image"
        const val COLUMN_NAME_PARENT_ID = "parent_id"
    }
}