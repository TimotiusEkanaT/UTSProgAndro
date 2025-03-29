package com.example.safevault.data.source.remote

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.android.gms.tasks.Task
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseService @Inject constructor() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun signInWithEmail(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    fun createUserWithEmail(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    fun signInWithGoogle(idToken: String): Task<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return auth.signInWithCredential(credential)
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    fun getCurrentUserDisplayName(): String? {
        return auth.currentUser?.displayName
    }

    // Firestore operations
    fun getUserDocument(userId: String) = firestore.collection("users").document(userId)

    fun getNoteCollection(userId: String) = firestore.collection("users").document(userId).collection("notes")

    fun getHiddenNoteCollection(userId: String) = firestore.collection("users").document(userId).collection("hidden_notes")

    suspend fun saveUserData(userId: String, userData: Map<String, Any>) {
        firestore.collection("users").document(userId).set(userData).await()
    }

    suspend fun updateUserData(userId: String, userData: Map<String, Any>) {
        firestore.collection("users").document(userId).update(userData).await()
    }
}