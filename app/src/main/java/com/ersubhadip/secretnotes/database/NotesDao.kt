package com.ersubhadip.secretnotes.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Insert
    fun createNote(note: NotesEntity)

    @Query("SELECT * FROM notes")
    fun getNotes(): Flow<List<NotesEntity>>

    @Delete
    fun deleteNote(note: NotesEntity)
}