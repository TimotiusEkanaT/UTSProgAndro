package com.example.safevault.ui.hidden

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.safevault.data.model.User
import com.example.safevault.data.repository.UserRepository
import kotlinx.coroutines.launch

class HiddenSettingViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val currentUserId: String?
        get() = userRepository.getCurrentUserId()

    val user: LiveData<User?> = currentUserId?.let {
        userRepository.getUserById(it).asLiveData()
    } ?: androidx.lifecycle.MutableLiveData(null)

    fun updateHiddenNoteSettings(enabled: Boolean, keyword: String?) {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            userRepository.updateHiddenNoteSettings(userId, enabled, keyword)
        }
    }

    fun isHiddenNoteEnabled(): Boolean {
        return user.value?.hiddenNoteEnabled == true
    }

    fun getDecryptedKeyword(): String? {
        return user.value?.hiddenNoteKeyword?.let {
            userRepository.getDecryptedKeyword(it)
        }
    }
}