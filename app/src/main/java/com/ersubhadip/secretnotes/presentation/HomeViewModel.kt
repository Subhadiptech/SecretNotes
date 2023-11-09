package com.ersubhadip.secretnotes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ersubhadip.secretnotes.database.NotesDao
import com.ersubhadip.secretnotes.database.NotesEntity
import com.ersubhadip.secretnotes.database.SecretDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel : ViewModel(), KoinComponent {
    private val db: SecretDatabase by inject()
    val dao: NotesDao = db.notesDao()

    private val _notes = MutableStateFlow<List<NotesEntity>>(emptyList())
    val notes = _notes.asStateFlow()

    init {
        getAll()
    }

    private fun getAll() {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getNotes().collect { result ->
                _notes.update { result }
            }
        }
    }

    fun createOne(note: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.createNote(NotesEntity(note = note))
        }
    }

    fun deleteOne(note: NotesEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteNote(note)
        }
    }
}