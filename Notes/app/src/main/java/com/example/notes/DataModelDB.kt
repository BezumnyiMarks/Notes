package com.example.notes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Item")
data class Item(
    @PrimaryKey
    @ColumnInfo(name = "dateTime")
    val dateTime: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "description")
    val description: String,
    @ColumnInfo(name = "imageURI")
    val imageURI: String,
    @ColumnInfo(name = "relevant")
    val relevant: Boolean
)
