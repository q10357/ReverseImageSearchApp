package no.kristiania.android.reverseimagesearchapp.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import no.kristiania.android.reverseimagesearchapp.core.util.provider.Constants
import no.kristiania.android.reverseimagesearchapp.data.local.FeedReaderContract.UploadedImageTable
import javax.inject.Inject

class ImageDatabaseHelper @Inject constructor(context: Context): SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ${FeedReaderContract.ResultImageTable.TABLE_NAME}" +
                "( ${FeedReaderContract.ResultImageTable.ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                " ${FeedReaderContract.ResultImageTable.COLUMN_NAME_IMAGE} blob"
                +" )" )

        db.execSQL(
            "INSERT INTO ${FeedReaderContract.ResultImageTable.TABLE_NAME} VALUES(1, 'First test');"
        )

        db.execSQL("CREATE TABLE ${UploadedImageTable.TABLE_NAME}" +
                " ( ${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${UploadedImageTable.COLUMN_NAME_TITLE} TEXT, " +
                "${UploadedImageTable.COLUMN_NAME_IMAGE} BLOB," +
                 " FOREIGN KEY (${FeedReaderContract.ResultImageTable.ID}) references ${FeedReaderContract.ResultImageTable.TABLE_NAME}"
                         + "(${FeedReaderContract.ResultImageTable.ID})")



    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${UploadedImageTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${FeedReaderContract.ResultImageTable.TABLE_NAME}")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = Constants.DATABASE_NAME
        const val DATABASE_VERSION = 3
    }

}
object FeedReaderContract {
    object UploadedImageTable: BaseColumns {
        const val TABLE_NAME = "uploaded_images"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_IMAGE = "image"
    }

    object ResultImageTable: BaseColumns {
        const val TABLE_NAME = "result_images"
        var ID = "result_id"
        const val COLUMN_NAME_IMAGE = "image"
    }
}