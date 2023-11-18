package com.ersubhadip.secretnotes

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ersubhadip.secretnotes.database.NotesDao
import com.ersubhadip.secretnotes.database.NotesEntity
import com.ersubhadip.secretnotes.database.SecretDatabase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NotesDaoTest {

    lateinit var notesDatabase: SecretDatabase
    lateinit var notesDao: NotesDao


//    @get:Rule
//    val instantExecutorRule = InstantTaskExecutorRule()   //this will sync execute

    @Before
    fun setUp() {
        notesDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SecretDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        notesDao = notesDatabase.notesDao()
    }

    @Test
    fun test_insertNotesData() = runBlocking {
        val note = NotesEntity(0, "Test case")
        notesDao.createNote(note)
        val result = notesDao.getNotes().collectLatest {
            assertEquals(1, it.size)
        }  //this is a flow so we have to wait for the threads
    }

    @After
    fun tearDown() {
        notesDatabase.close()
    }
}