package com.example.safevault.ui.hidden

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.ViewModelProvider
import com.example.safevault.safevault.SafeVaultApplication
import com.example.safevault.databinding.ActivityHiddenNoteAddfingerBinding
import com.example.safevault.utils.BiometricUtil
import com.example.safevault.utils.showToast

class HiddenNoteAddFingerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHiddenNoteAddfingerBinding
    private lateinit var viewModel: HiddenNoteViewModel
    private lateinit var biometricUtil: BiometricUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHiddenNoteAddfingerBinding.inflate(layoutInflater)
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

        binding.ivBack.setOnClickListener {
            setupFingerprint()
        }
    }

    private fun setupFingerprint() {
        if (!biometricUtil.isFingerprintAvailable()) {
            showToast("Fingerprint authentication not available on this device")
            finish()
            return
        }

        biometricUtil.showFingerprintPrompt(
            this,
            onSuccess = {
                // Register fingerprint for the user
                val userId = viewModel.user.value?.id ?: return@showFingerprintPrompt
                viewModel.setupFingerprint(userId)
                showToast("Fingerprint registered successfully")
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