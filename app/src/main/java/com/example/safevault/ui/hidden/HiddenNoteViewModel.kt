package com.example.safevault.ui.hidden

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.safevault.data.model.HiddenNote
import com.example.safevault.data.model.User
import com.example.safevault.data.repository.NoteRepository
import com.example.safevault.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class HiddenNoteViewModel(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _authStatus = MutableLiveData<AuthStatus>()

    val authStatus: LiveData<AuthStatus> = _authStatus

    enum class AuthStatus {
        AUTHORIZED, UNAUTHORIZED, NEEDS_SETUP
    }

    private val currentUserId: String?
        get() = userRepository.getCurrentUserId()

    val user: LiveData<User?> = currentUserId?.let {
        userRepository.getUserById(it).asLiveData()
    } ?: MutableLiveData(null)

    val hiddenNotes: LiveData<List<HiddenNote>> = _searchQuery.flatMapLatest { query ->
        if (query.isEmpty()) {
            currentUserId?.let { noteRepository.getAllHiddenNotes(it) } ?: MutableStateFlow(emptyList())
        } else {
            currentUserId?.let { noteRepository.searchHiddenNotes(it, query) } ?: MutableStateFlow(emptyList())
        }
    }.asLiveData()

    fun search(query: String) {
        viewModelScope.launch {
            _searchQuery.value = query
        }
    }

    fun deleteHiddenNote(hiddenNote: HiddenNote) {
        viewModelScope.launch {
            noteRepository.deleteHiddenNote(hiddenNote)
        }
    }

    fun checkAuthStatus() {
        val user = user.value
        if (user == null) {
            _authStatus.value = AuthStatus.UNAUTHORIZED
            return
        }

        if (!user.hasFingerprintData && !user.hasFaceIdData && !user.hasPin) {
            _authStatus.value = AuthStatus.NEEDS_SETUP
        } else {
            _authStatus.value = AuthStatus.UNAUTHORIZED
        }
    }

    fun authorize() {
        _authStatus.value = AuthStatus.AUTHORIZED
    }

    fun getDecryptedNoteContent(hiddenNote: HiddenNote): String {
        return noteRepository.decryptHiddenNoteContent(hiddenNote)
    }

    fun verifyPin(inputPin: String): Boolean {
        val userId = currentUserId ?: return false
        val user = user.value ?: return false

        return userRepository.verifyPin(user.pin, inputPin)
    }

    fun setupFingerprint(userId: String) {
        viewModelScope.launch {
            userRepository.updateFingerprintStatus(userId, true)
        }
    }

    fun setupFaceId(userId: String) {
        viewModelScope.launch {
            userRepository.updateFaceIdStatus(userId, true)
        }
    }

    fun setupPin(userId: String, pin: String) {
        viewModelScope.launch {
            userRepository.updatePinStatus(userId, true, pin)
        }
    }
}