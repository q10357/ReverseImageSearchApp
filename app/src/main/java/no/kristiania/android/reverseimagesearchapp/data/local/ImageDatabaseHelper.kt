package no.kristiania.android.reverseimagesearchapp.data.local.sqlLite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import no.kristiania.android.reverseimagesearchapp.core.util.Constants
import no.kristiania.android.reverseimagesearchapp.data.local.sqlLite.FeedReaderContract.FeedEntry
import javax.inject.Inject

class ImageDatabaseHelper @Inject constructor(context: Context): SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE ${FeedEntry.TABLE_NAME}" +
                " (${BaseColumns._ID} INTEGER PRIMARY KEY, " +
                "${FeedEntry.COLUMN_NAME_TITLE} TEXT, " +
                "${FeedEntry.COLUMN_NAME_IMAGE} BLOB)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${FeedEntry.TABLE_NAME}")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = Constants.DATABASE_NAME
        const val DATABASE_VERSION = 1
    }
}
object FeedReaderContract {
    object FeedEntry: BaseColumns {
        const val TABLE_NAME = "uploaded_images"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_IMAGE = "image"
    }
}