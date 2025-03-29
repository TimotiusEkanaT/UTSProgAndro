package com.example.safevault.di

import android.content.Context
import com.example.safevault.data.repository.NoteRepository
import com.example.safevault.data.repository.UserRepository
import com.example.safevault.data.source.local.NoteDao
import com.example.safevault.data.source.local.NoteDatabase
import com.example.safevault.data.source.local.UserDao
import com.example.safevault.data.source.remote.FirebaseService
import com.example.safevault.utils.Encryption

/**
 * Ini bukan modul Dagger/Hilt lagi, tapi kelas utility untuk menyediakan dependency
 * secara manual. Ini akan digunakan oleh SafeVaultApplication.
 */
object AppModule {

    fun provideNoteDatabase(context: Context): NoteDatabase {
        return NoteDatabase.getDatabase(context)
    }

    fun provideNoteDao(database: NoteDatabase): NoteDao {
        return database.noteDao()
    }

    fun provideUserDao(database: NoteDatabase): UserDao {
        return database.userDao()
    }

    fun provideFirebaseService(): FirebaseService {
        return FirebaseService()
    }

    fun provideEncryption(context: Context): Encryption {
        return Encryption(context)
    }

    fun provideNoteRepository(noteDao: NoteDao, encryption: Encryption): NoteRepository {
        return NoteRepository(noteDao, encryption)
    }

    fun provideUserRepository(
        userDao: UserDao,
        firebaseService: FirebaseService,
        encryption: Encryption
    ): UserRepository {
        return UserRepository(userDao, firebaseService, encryption)
    }
}