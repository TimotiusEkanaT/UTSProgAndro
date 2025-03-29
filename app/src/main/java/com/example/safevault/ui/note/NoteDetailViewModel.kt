package com.example.safevault.ui.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safevault.data.model.Note
import com.example.safevault.data.repository.NoteRepository
import com.example.safevault.data.repository.UserRepository
import kotlinx.coroutines.launch
import java.util.Date

class NoteDetailViewModel(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _note = MutableLiveData<Note?>()
    val note: LiveData<Note?> = _note

    private val _savingStatus = MutableLiveData<SavingStatus>()
    val savingStatus: LiveData<SavingStatus> = _savingStatus

    sealed class SavingStatus {
        object Success : SavingStatus()
        data class Error(val message: String) : SavingStatus()
    }

    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            val loadedNote = noteRepository.getNoteById(noteId)
            _note.value = loadedNote
        }
    }

    fun createNote(title: String, content: String) {
        viewModelScope.launch {
            try {
                val userId = userRepository.getCurrentUserId() ?: throw Exception("User not logged in")
                val newNote = Note(
                    title = title,
                    content = content,
                    userId = userId
                )
                noteRepository.insertNote(newNote)
                _savingStatus.value = SavingStatus.Success
            } catch (e: Exception) {
                _savingStatus.value = SavingStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateNote(noteId: Long, title: String, content: String) {
        viewModelScope.launch {
            try {
                val existingNote = noteRepository.getNoteById(noteId)
                    ?: throw Exception("Note not found")

                val updatedNote = existingNote.copy(
                    title = title,
                    content = content,
                    updatedAt = Date()
                )

                noteRepository.updateNote(updatedNote)
                _savingStatus.value = SavingStatus.Success
            } catch (e: Exception) {
                _savingStatus.value = SavingStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch {
            try {
                val noteToDelete = noteRepository.getNoteById(noteId)
                    ?: throw Exception("Note not found")

                noteRepository.deleteNote(noteToDelete)
            } catch (e: Exception) {
                _savingStatus.value = SavingStatus.Error(e.message ?: "Unknown error")
            }
        }
    }
}