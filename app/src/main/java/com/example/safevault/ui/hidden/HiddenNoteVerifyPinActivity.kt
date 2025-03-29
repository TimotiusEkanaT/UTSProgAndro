package com.example.safevault.ui.hidden

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.safevault.safevault.SafeVaultApplication
import com.example.safevault.databinding.ActivityHiddenNoteVerpinBinding
import com.example.safevault.utils.Constants
import com.example.safevault.utils.showToast

class HiddenNoteVerifyPinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHiddenNoteVerpinBinding
    private lateinit var viewModel: HiddenNoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHiddenNoteVerpinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setupListeners()
    }

    private fun initViewModel() {
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

        binding.ivNext.setOnClickListener {
            val pin = binding.etPin.text.toString()
            if (pin.isEmpty()) {
                showToast("Please enter your PIN")
                return@setOnClickListener
            }

            verifyPin(pin)
        }
    }

    private fun verifyPin(pin: String) {
        if (viewModel.verifyPin(pin)) {
            setResult(Constants.RESULT_AUTH_SUCCESS)
            finish()
        } else {
            showToast("Incorrect PIN")
            binding.etPin.text?.clear()
        }
    }

    override fun onBackPressed() {
        setResult(Constants.RESULT_AUTH_FAILED)
        super.onBackPressed()
    }
}