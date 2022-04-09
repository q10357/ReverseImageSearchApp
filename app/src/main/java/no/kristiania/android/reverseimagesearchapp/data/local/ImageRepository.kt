package no.kristiania.android.reverseimagesearchapp.data.local

import android.content.Context
import androidx.room.Room
import no.kristiania.android.reverseimagesearchapp.core.util.Constants
import java.lang.IllegalStateException

class ImageRepository private constructor(context: Context) {
    private val database: ImageDatabase = Room.databaseBuilder(
        context.applicationContext,
        ImageDatabase::class.java,
        Constants.DATABASE_NAME
    ).build()

    companion object {
        //Volatile means that whenever we change the value of the instance, other threads
        //will be aware, avoiding race conditions
        @Volatile
        private var instance: ImageRepository? = null

        fun initialize(context: Context) {
            //Syncronized makes sure that whenever this code is
            //executed by one thread, we lock the database to this thread
            //in this time, preserves singleton
//            synchronized(this) {
//                return instance ?: Room.databaseBuilder(
//                    context.applicationContext,
//                    ImageDatabase::class.java,
//                    "school_db"
//                ).build().also {
//                    instance = it
//                }
            if(instance == null){
                instance = ImageRepository(context)
            }
        }

        fun get(): ImageRepository {
            return instance ?: throw IllegalStateException("ImageRepository must be initialized")
        }
    }
}