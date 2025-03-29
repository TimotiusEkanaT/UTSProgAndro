package com.example.safevault.ui.hidden

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.safevault.safevault.SafeVaultApplication
import com.example.safevault.data.repository.UserRepository
import com.example.safevault.databinding.ActivityHiddenSettingOnBinding
import com.example.safevault.utils.showToast

class HiddenSettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHiddenSettingOnBinding
    private lateinit var viewModel: HiddenSettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHiddenSettingOnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        setupListeners()
        observeViewModel()
    }

    private fun initViewModel() {
        val factory = HiddenSettingViewModelFactory(
            (application as SafeVaultApplication).userRepository
        )
        viewModel = ViewModelProvider(this, factory)[HiddenSettingViewModel::class.java]
    }

    private fun setupListeners() {
        binding.ivBack.setOnClickListener {
            finish()
        }

        binding.switchHiddenNotes.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etKeyword.visibility = View.VISIBLE
            } else {
                binding.etKeyword.visibility = View.GONE
                // If turning off hidden notes, update without keyword
                updateHiddenNoteSettings(false, null)
            }
        }

        binding.btnSave.setOnClickListener {
            val isEnabled = binding.switchHiddenNotes.isChecked
            val keyword = if (isEnabled) binding.etKeyword.text.toString() else null

            if (isEnabled && keyword.isNullOrEmpty()) {
                showToast("Please enter a keyword")
                return@setOnClickListener
            }

            updateHiddenNoteSettings(isEnabled, keyword)
            showToast("Settings saved")
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(this) { user ->
            user?.let {
                binding.switchHiddenNotes.isChecked = it.hiddenNoteEnabled
                binding.etKeyword.visibility = if (it.hiddenNoteEnabled) View.VISIBLE else View.GONE

                // If hidden notes are enabled, show the decrypted keyword
                if (it.hiddenNoteEnabled) {
                    val decryptedKeyword = viewModel.getDecryptedKeyword()
                    binding.etKeyword.setText(decryptedKeyword)
                }
            }
        }
    }

    private fun updateHiddenNoteSettings(enabled: Boolean, keyword: String?) {
        viewModel.updateHiddenNoteSettings(enabled, keyword)
    }
}

class HiddenSettingViewModelFactory(
    private val userRepository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HiddenSettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HiddenSettingViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}