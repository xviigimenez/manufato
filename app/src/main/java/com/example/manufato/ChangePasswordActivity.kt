package com.example.manufato

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var currentPasswordInput: EditText
    private lateinit var newPasswordInput: EditText
    private lateinit var confirmNewPasswordInput: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: TextView
    private lateinit var userPrefs: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        userPrefs = UserPreferences(this)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        currentPasswordInput = findViewById(R.id.currentPasswordInput)
        newPasswordInput = findViewById(R.id.newPasswordInput)
        confirmNewPasswordInput = findViewById(R.id.confirmNewPasswordInput)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
        }

        cancelButton.setOnClickListener {
            finish()
        }

        saveButton.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        val currentPassword = currentPasswordInput.text.toString()
        val newPassword = newPasswordInput.text.toString()
        val confirmNewPassword = confirmNewPasswordInput.text.toString()

        val storedPassword = userPrefs.getUserPassword()

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentPassword != storedPassword) {
            currentPasswordInput.error = "Senha atual incorreta"
            return
        }

        if (newPassword.length < 6) {
            newPasswordInput.error = "A nova senha deve ter pelo menos 6 caracteres"
            return
        }

        if (newPassword != confirmNewPassword) {
            confirmNewPasswordInput.error = "As senhas não coincidem"
            return
        }

        userPrefs.updateUserPassword(newPassword)
        Toast.makeText(this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show()
        finish()
    }
}
