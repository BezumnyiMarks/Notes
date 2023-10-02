package com.example.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DBViewModel(private val notesDao: NotesDao): ViewModel() {

    fun addItem(item: Item){
        viewModelScope.launch {
            notesDao.insert(item)
        }
    }

    suspend fun getByDateTime(dateTime: Long): Item{
        return notesDao.getByDateTime(dateTime)
    }

    suspend fun getAll(): List<Item>{
        return notesDao.getAll()
    }

    fun delete(itemsList: List<Item>){
        viewModelScope.launch {
           notesDao.delete(itemsList)
        }
    }
}