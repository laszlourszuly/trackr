package com.echsylon.komoot.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This class describes pictures meta-data table in the database.
 */
@Entity(tableName = "picture")
data class Picture(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    val id: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "owner")
    val owner: String,

    @ColumnInfo(name = "lat")
    val latitude: Double,

    @ColumnInfo(name = "lng")
    val longitude: Double,

    @ColumnInfo(name = "url")
    val url: String,

    @ColumnInfo(name = "size_width")
    val width: Int,

    @ColumnInfo(name = "size_height")
    val height: Int,

    @ColumnInfo(name = "date_uploaded")
    val uploaded: Long,

    @ColumnInfo(name = "date_cached")
    val cached: Long = System.currentTimeMillis()
)