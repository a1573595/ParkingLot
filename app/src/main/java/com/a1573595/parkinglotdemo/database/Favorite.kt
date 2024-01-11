package com.a1573595.parkinglotdemo.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = TABLE_FAVORITE)
data class Favorite(
    @PrimaryKey
    val id: String,
)