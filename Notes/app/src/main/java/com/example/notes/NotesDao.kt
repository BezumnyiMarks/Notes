package com.example.notes

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface NotesDao {

    @Query("SELECT * FROM Item")
    suspend fun getAll(): List<Item>

    @Query("SELECT * FROM Item WHERE dateTime = :dateTime")
    suspend fun getByDateTime(dateTime: Long): Item

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item)

    @Delete
    suspend fun delete(filmInfoList: List<Item>)

}