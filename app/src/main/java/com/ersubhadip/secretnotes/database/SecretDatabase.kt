package com.ersubhadip.secretnotes.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NotesEntity::class], version = 1)
abstract class SecretDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}