package com.example.safevault.ui.hidden

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.ViewModelProvider
import com.example.safevault.databinding.ActivityHiddenNoteVerfingerBinding
import com.example.safevault.safevault.SafeVaultApplication
import com.example.safevault.utils.BiometricUtil
import com.example.safevault.utils.Constants
import com.example.safevault.utils.showToast

class HiddenNoteVerifyFingerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHiddenNoteVerfingerBinding
    private lateinit var viewModel: HiddenNoteViewModel
    private lateinit var biometricUtil: BiometricUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHiddenNoteVerfingerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi manual dependencies
        initDependencies()
        setupListeners()
        showFingerprintPrompt()
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
            setResult(Constants.RESULT_AUTH_FAILED)
            finish()
        }
    }

    private fun showFingerprintPrompt() {
        if (!biometricUtil.isFingerprintAvailable()) {
            showToast("Fingerprint authentication not available")
            setResult(Constants.RESULT_AUTH_FAILED)
            finish()
            return
        }

        biometricUtil.showFingerprintPrompt(
            this,
            onSuccess = {
                setResult(Constants.RESULT_AUTH_SUCCESS)
                finish()
            },
            onError = { errorCode, errString ->
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_USER_CANCELED) {
                    // User canceled
                    setResult(Constants.RESULT_AUTH_FAILED)
                    finish()
                } else {
                    showToast("Authentication error: $errString")
                    // Give another chance to authenticate
                }
            }
        )
    }

    override fun onBackPressed() {
        setResult(Constants.RESULT_AUTH_FAILED)
        super.onBackPressed()
    }
}