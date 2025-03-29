package com.example.safevault.ui.hidden

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safevault.data.model.HiddenNote
import com.example.safevault.data.repository.NoteRepository
import com.example.safevault.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date

class HiddenNoteDetailViewModel(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _hiddenNote = MutableLiveData<HiddenNote?>()
    val hiddenNote: LiveData<HiddenNote?> = _hiddenNote

    private val _savingStatus = MutableLiveData<SavingStatus>()
    val savingStatus: LiveData<SavingStatus> = _savingStatus

    sealed class SavingStatus {
        object Success : SavingStatus()
        data class Error(val message: String) : SavingStatus()
    }

    fun loadHiddenNote(noteId: Long) {
        viewModelScope.launch {
            val loadedNote = noteRepository.getHiddenNoteById(noteId)
            _hiddenNote.value = loadedNote
        }
    }

    fun createHiddenNote(title: String, content: String) {
        viewModelScope.launch {
            try {
                val userId = userRepository.getCurrentUserId() ?: throw Exception("User not logged in")

                // Menggunakan first() untuk mendapatkan nilai pertama dari Flow
                val user = userRepository.getUserById(userId).first()
                    ?: throw Exception("User not found")

                val keyword = user.hiddenNoteKeyword?.let {
                    userRepository.getDecryptedKeyword(it)
                } ?: throw Exception("No hidden note keyword set")

                val newHiddenNote = HiddenNote(
                    title = title,
                    content = "", // Content will be encrypted and set by repository
                    userId = userId,
                    accessKeyword = keyword
                )

                val noteId = noteRepository.insertHiddenNote(newHiddenNote, content)
                if (noteId > 0) {
                    _savingStatus.value = SavingStatus.Success
                } else {
                    _savingStatus.value = SavingStatus.Error("Failed to save note")
                }
            } catch (e: Exception) {
                _savingStatus.value = SavingStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateHiddenNote(noteId: Long, title: String, content: String) {
        viewModelScope.launch {
            try {
                val existingNote = noteRepository.getHiddenNoteById(noteId)
                    ?: throw Exception("Note not found")

                val updatedNote = existingNote.copy(
                    title = title,
                    updatedAt = Date()
                )

                noteRepository.updateHiddenNote(updatedNote, content)
                _savingStatus.value = SavingStatus.Success
            } catch (e: Exception) {
                _savingStatus.value = SavingStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteHiddenNote(noteId: Long) {
        viewModelScope.launch {
            try {
                val noteToDelete = noteRepository.getHiddenNoteById(noteId)
                    ?: throw Exception("Note not found")

                noteRepository.deleteHiddenNote(noteToDelete)
            } catch (e: Exception) {
                _savingStatus.value = SavingStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getDecryptedContent(hiddenNote: HiddenNote): String {
        return noteRepository.decryptHiddenNoteContent(hiddenNote)
    }
}