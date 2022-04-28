package no.kristiania.android.reverseimagesearchapp.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import no.kristiania.android.reverseimagesearchapp.core.util.provider.Constants
import no.kristiania.android.reverseimagesearchapp.data.local.FeedReaderContract.ResultImageTable
import no.kristiania.android.reverseimagesearchapp.data.local.FeedReaderContract.UploadedImageTable
import javax.inject.Inject

class ImageDatabaseHelper @Inject constructor(context: Context): SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    //our DatabaseHelper which creates the database and uses variables for names incase we want to
    //rename them, then we only change them once
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ${UploadedImageTable.TABLE_NAME}" +
                " ( ${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${UploadedImageTable.COLUMN_NAME_TITLE} TEXT, " +
                "${UploadedImageTable.COLUMN_NAME_IMAGE} BLOB," +
                "${UploadedImageTable.COLUMN_NAME_DATE} DATETIME DEFAULT CURRENT_TIMESTAMP);")

        db.execSQL("CREATE TABLE ${ResultImageTable.TABLE_NAME}" +
                "( ${BaseColumns._ID} INTEGER PRIMARY KEY," +
                " ${ResultImageTable.COLUMN_NAME_IMAGE} BLOB, " +
                "${ResultImageTable.COLUMN_NAME_PARENT_ID} INTEGER, " +
                "CONSTRAINT FK_RESULTS FOREIGN KEY (${ResultImageTable.COLUMN_NAME_PARENT_ID})" +
                "REFERENCES ${UploadedImageTable.TABLE_NAME}(${BaseColumns._ID}))")
        //"${ResultImageTable.COLUMN_NAME_PARENT_ID} INTEGER REFERENCES ${UploadedImageTable.TABLE_NAME});")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${UploadedImageTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${ResultImageTable.TABLE_NAME}")
        onCreate(db)
    }
    override fun onOpen(db: SQLiteDatabase?){
        super.onOpen(db);
        db?.execSQL("PRAGMA foreign_keys = ON;")
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
        const val COLUMN_NAME_DATE = "date"
    }

    object ResultImageTable: BaseColumns {
        const val TABLE_NAME = "result_images"
        const val COLUMN_NAME_IMAGE = "image"
        const val COLUMN_NAME_PARENT_ID = "parent_id"
    }
}