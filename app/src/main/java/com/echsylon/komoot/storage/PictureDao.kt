package com.echsylon.komoot.storage

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * This class offers means of accessing the pictures meta-data in the database.
 */
@Dao
interface PictureDao {

    /**
     * Returns a live data object reflecting the state of all picture data in
     * the database at any time. The Room infrastructure ensures that the data
     * is retrieved in a non-blocking manner.
     *
     * @return The pictures meta-data reactive accessor object.
     */
    @Query("SELECT * FROM picture ORDER BY date_cached DESC")
    fun getAllPictures(): LiveData<List<Picture>>

    /**
     * Inserts new picture meta-data into the database. This method must be
     * called from a coroutine context.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(picture: Picture)

    /**
     * Deletes all picture meta-data from the database. This method must be
     * called from a coroutine context.
     */
    @Query("DELETE FROM picture")
    suspend fun deleteAll()

}