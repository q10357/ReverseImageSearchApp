package no.kristiania.android.reverseimagesearchapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import no.kristiania.android.reverseimagesearchapp.core.util.Constants
import no.kristiania.android.reverseimagesearchapp.data.local.entity.ReverseImageSearchItem
import no.kristiania.android.reverseimagesearchapp.data.local.entity.UploadedImage

@Database(
    entities = [
        UploadedImage::class,
        ReverseImageSearchItem::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ImageDatabase: RoomDatabase() {
    abstract val imageDao: ImageDao

    companion object {
        //Volatile means that whenever we change the value of the instance, other threads
        //will be aware, avoiding race conditions
        @Volatile
        private var instance: ImageDatabase? = null

        fun initialize(context: Context): ImageDatabase {
//            Syncronized makes sure that whenever this code is
//            executed by one thread, we lock the database to this thread
//            in this time, preserves singleton
            synchronized(this) {
                return instance ?: Room.databaseBuilder(
                    context,
                    ImageDatabase::class.java,
                    Constants.DATABASE_NAME
                ).build().also {
                    instance = it
                }
            }
        }
    }
}