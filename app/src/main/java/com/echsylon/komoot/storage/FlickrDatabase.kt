package com.echsylon.komoot.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * This class represents the singleton instance of the local pictures meta-data
 * repository. It knows how to manage the underlying database (create, migrate
 * etc) and offers convenient means of accessing the data.
 */
@Database(
    entities = [Picture::class],
    version = 1,
    exportSchema = false
)
abstract class FlickrDatabase : RoomDatabase() {

    companion object {
        @Volatile
        private var INSTANCE: FlickrDatabase? = null

        fun getInstance(context: Context): FlickrDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room
                    .databaseBuilder(context.applicationContext, FlickrDatabase::class.java, "flickr.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    /**
     * Returns the picture meta-data access object.
     */
    abstract fun pictureDao(): PictureDao
}