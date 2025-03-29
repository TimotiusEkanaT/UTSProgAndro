package com.example.safevault.data.repository

import com.example.safevault.data.model.User
import com.example.safevault.data.source.local.UserDao
import com.example.safevault.data.source.remote.FirebaseService
import com.example.safevault.utils.Encryption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val userDao: UserDao,
    private val firebaseService: FirebaseService,
    private val encryption: Encryption
) {

    // Local user data operations
    fun getUserById(userId: String): Flow<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    // Firebase user authentication
    suspend fun signInWithEmail(email: String, password: String): String? {
        val authResult = firebaseService.signInWithEmail(email, password).await()
        val user = authResult.user
        return user?.uid
    }

    suspend fun signInWithGoogle(idToken: String): String? {
        val authResult = firebaseService.signInWithGoogle(idToken).await()
        val user = authResult.user
        return user?.uid
    }

    suspend fun createUserWithEmail(email: String, password: String): String? {
        val authResult = firebaseService.createUserWithEmail(email, password).await()
        val user = authResult.user
        return user?.uid
    }

    suspend fun signOut() {
        firebaseService.signOut()
    }

    fun getCurrentUserId(): String? {
        return firebaseService.getCurrentUserId()
    }

    // Metode-metode yang perlu ditambahkan
    fun getCurrentUserEmail(): String? {
        return firebaseService.getCurrentUserEmail()
    }

    fun getCurrentUserDisplayName(): String? {
        return firebaseService.getCurrentUserDisplayName()
    }

    // Biometric and security features
    suspend fun updateFingerprintStatus(userId: String, hasFingerprint: Boolean) {
        userDao.updateFingerprintStatus(userId, hasFingerprint)
    }

    suspend fun updateFaceIdStatus(userId: String, hasFaceId: Boolean) {
        userDao.updateFaceIdStatus(userId, hasFaceId)
    }

    suspend fun updatePinStatus(userId: String, hasPin: Boolean, pin: String?) {
        // Encrypt pin before storing
        val encryptedPin = pin?.let { encryption.encrypt(it) }
        userDao.updatePinStatus(userId, hasPin, encryptedPin)
    }

    suspend fun updateHiddenNoteSettings(userId: String, enabled: Boolean, keyword: String?) {
        // Encrypt keyword for security
        val encryptedKeyword = keyword?.let { encryption.encrypt(it) }
        userDao.updateHiddenNoteSettings(userId, enabled, encryptedKeyword)
    }

    fun verifyPin(storedPin: String?, inputPin: String): Boolean {
        return storedPin?.let {
            val decryptedPin = encryption.decrypt(it)
            decryptedPin == inputPin
        } ?: false
    }

    fun getDecryptedKeyword(encryptedKeyword: String?): String? {
        return encryptedKeyword?.let { encryption.decrypt(it) }
    }
}