package com.ersubhadip.secretnotes.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("notes")
data class NotesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,

    @ColumnInfo("notes")
    val note: String
)