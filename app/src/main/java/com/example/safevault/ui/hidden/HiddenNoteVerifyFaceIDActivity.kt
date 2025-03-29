package com.example.safevault.ui.hidden

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.ViewModelProvider
import com.example.safevault.safevault.SafeVaultApplication
import com.example.safevault.databinding.ActivityHiddenNoteVerfaceidBinding
import com.example.safevault.utils.BiometricUtil
import com.example.safevault.utils.Constants
import com.example.safevault.utils.showToast

class HiddenNoteVerifyFaceIDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHiddenNoteVerfaceidBinding
    private lateinit var viewModel: HiddenNoteViewModel
    private lateinit var biometricUtil: BiometricUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHiddenNoteVerfaceidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDependencies()
        setupListeners()
        showFaceIdPrompt()
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

    private fun showFaceIdPrompt() {
        if (!biometricUtil.isFaceRecognitionAvailable()) {
            showToast("Face recognition not available")
            setResult(Constants.RESULT_AUTH_FAILED)
            finish()
            return
        }

        biometricUtil.showFaceIdPrompt(
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