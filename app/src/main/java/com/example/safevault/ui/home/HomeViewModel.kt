package com.example.safevault.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.safevault.data.model.Note
import com.example.safevault.data.model.User
import com.example.safevault.data.repository.NoteRepository
import com.example.safevault.data.repository.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")

    private val currentUserId: String?
        get() = userRepository.getCurrentUserId()

    val user: LiveData<User?> = currentUserId?.let {
        userRepository.getUserById(it).asLiveData()
    } ?: MutableLiveData(null)

    val notes: LiveData<List<Note>> = _searchQuery.flatMapLatest { query ->
        if (query.isEmpty()) {
            currentUserId?.let { noteRepository.getAllNotes(it) } ?: MutableStateFlow(emptyList())
        } else {
            currentUserId?.let { noteRepository.searchNotes(it, query) } ?: MutableStateFlow(emptyList())
        }
    }.asLiveData()

    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _searchQuery.value = query
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteRepository.deleteNote(note)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
        }
    }

    fun isHiddenNoteEnabled(): Boolean {
        return (user.value?.hiddenNoteEnabled == true)
    }

    fun getHiddenNoteKeyword(): String? {
        return user.value?.hiddenNoteKeyword?.let {
            userRepository.getDecryptedKeyword(it)
        }
    }
}