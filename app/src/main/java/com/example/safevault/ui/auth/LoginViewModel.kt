package com.example.safevault.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safevault.data.model.User
import com.example.safevault.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    sealed class LoginResult {
        data class Success(val userId: String) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }

    fun signInWithEmail(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val userId = userRepository.signInWithEmail(email, password)
                if (userId != null) {
                    _loginResult.value = LoginResult.Success(userId)
                } else {
                    _loginResult.value = LoginResult.Error("Authentication failed")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error(e.message ?: "Unknown error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val userId = userRepository.signInWithGoogle(idToken)
                if (userId != null) {
                    // Check if user exists in our database, if not create a new user
                    userRepository.getUserById(userId).collect { user ->
                        if (user == null) {
                            // Create new user in our database
                            val email = userRepository.getCurrentUserEmail() ?: ""
                            val displayName = userRepository.getCurrentUserDisplayName() ?: ""
                            val newUser = User(
                                id = userId,
                                email = email,
                                displayName = displayName
                            )
                            userRepository.insertUser(newUser)
                        }
                        _loginResult.value = LoginResult.Success(userId)
                    }
                } else {
                    _loginResult.value = LoginResult.Error("Google authentication failed")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error(e.message ?: "Unknown error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createUserWithEmail(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val userId = userRepository.createUserWithEmail(email, password)
                if (userId != null) {
                    // Create user in our database
                    val newUser = User(
                        id = userId,
                        email = email
                    )
                    userRepository.insertUser(newUser)
                    _loginResult.value = LoginResult.Success(userId)
                } else {
                    _loginResult.value = LoginResult.Error("User creation failed")
                }
            } catch (e: Exception) {
                _loginResult.value = LoginResult.Error(e.message ?: "Unknown error occurred")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return userRepository.getCurrentUserId() != null
    }
}