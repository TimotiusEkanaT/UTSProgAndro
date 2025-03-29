package com.example.safevault.ui.hidden

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.safevault.safevault.SafeVaultApplication
import com.example.safevault.data.repository.NoteRepository
import com.example.safevault.data.repository.UserRepository
import com.example.safevault.databinding.ActivityHiddenNoteAddfaceidBinding
import com.example.safevault.utils.BiometricUtil
import com.example.safevault.utils.showToast

class HiddenNoteAddFaceIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHiddenNoteAddfaceidBinding
    private lateinit var viewModel: HiddenNoteViewModel
    private lateinit var biometricUtil: BiometricUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHiddenNoteAddfaceidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi manual dependencies
        initDependencies()
        setupListeners()
    }

    private fun initDependencies() {
        // Inisialisasi BiometricUtil
        biometricUtil = BiometricUtil(applicationContext)

        // Inisialisasi ViewModel dengan factory pattern
        val factory = HiddenNoteViewModelFactory(
            (application as SafeVaultApplication).noteRepository,
            (application as SafeVaultApplication).userRepository
        )
        viewModel = ViewModelProvider(this, factory)[HiddenNoteViewModel::class.java]
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.ivNext.setOnClickListener {
            setupFaceId()
        }
    }

    private fun setupFaceId() {
        if (!biometricUtil.isFaceRecognitionAvailable()) {
            showToast("Face recognition not available on this device")
            finish()
            return
        }

        biometricUtil.showFaceIdPrompt(
            this,
            onSuccess = {
                // Register Face ID for the user
                val userId = viewModel.user.value?.id ?: return@showFaceIdPrompt
                viewModel.setupFaceId(userId)
                showToast("Face ID registered successfully")
                finish()
            },
            onError = { errorCode, errString ->
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                    // User canceled
                    finish()
                } else {
                    showToast("Authentication error: $errString")
                }
            }
        )
    }
}

// Factory untuk HiddenNoteViewModel
class HiddenNoteViewModelFactory(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HiddenNoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HiddenNoteViewModel(noteRepository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}