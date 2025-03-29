package com.example.safevault.safevault

import android.app.Application
import com.example.safevault.data.repository.NoteRepository
import com.example.safevault.data.repository.UserRepository
import com.example.safevault.data.source.local.NoteDatabase
import com.example.safevault.data.source.remote.FirebaseService
import com.example.safevault.utils.Encryption
import com.google.firebase.FirebaseApp

class SafeVaultApplication : Application() {

    // Lazy initialization for repositories
    val noteRepository: NoteRepository by lazy {
        val database = NoteDatabase.getDatabase(applicationContext)
        val encryption = Encryption(applicationContext)
        NoteRepository(database.noteDao(), encryption)
    }

    val userRepository: UserRepository by lazy {
        val database = NoteDatabase.getDatabase(applicationContext)
        val firebaseService = FirebaseService()
        val encryption = Encryption(applicationContext)
        UserRepository(database.userDao(), firebaseService, encryption)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}