//package com.bignerdranch.android.myapplication.data.sqlLite
//
//import android.content.Context
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//import com.bignerdranch.android.myapplication.provider.Constants
//import javax.inject.Inject
//
//class ImageDatabaseHelper @Inject constructor(context: Context): SQLiteOpenHelper(
//    context, DATABASE_NAME, null, DATABASE_VERSION
//) {
//    override fun onCreate(db: SQLiteDatabase) {
//        db.execSQL("CREATE TABLE uploaded_images (id primary key, title text, image blob)")
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        db.execSQL("DROP TABLE IF EXISTS uploaded_images")
//        onCreate(db)
//    }
//
//    companion object {
//        const val DATABASE_NAME = Constants.DATABASE_NAME
//        const val DATABASE_VERSION = 1
//    }
//}