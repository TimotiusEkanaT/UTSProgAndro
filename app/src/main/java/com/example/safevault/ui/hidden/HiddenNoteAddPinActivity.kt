package com.example.safevault.ui.hidden

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.safevault.safevault.SafeVaultApplication
import com.example.safevault.databinding.ActivityHiddenNoteAddPinBinding
import com.example.safevault.utils.Constants
import com.example.safevault.utils.showToast

class HiddenNoteAddPinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHiddenNoteAddPinBinding
    private lateinit var viewModel: HiddenNoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHiddenNoteAddPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setupListeners()
    }

    private fun initViewModel() {
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
            val pin = binding.etPin.text.toString()
            val confirmPin = binding.etConfirmPin.text.toString()

            if (pin.isEmpty()) {
                showToast("Please enter a PIN")
                return@setOnClickListener
            }

            if (pin.length < Constants.DEFAULT_PIN_LENGTH) {
                showToast("PIN must be at least ${Constants.DEFAULT_PIN_LENGTH} digits")
                return@setOnClickListener
            }

            if (pin != confirmPin) {
                showToast("PINs do not match")
                return@setOnClickListener
            }

            savePin(pin)
        }
    }

    private fun savePin(pin: String) {
        val userId = viewModel.user.value?.id
        if (userId == null) {
            showToast("User data not available")
            return
        }

        viewModel.setupPin(userId, pin)
        showToast("PIN set successfully")
        finish()
    }
}