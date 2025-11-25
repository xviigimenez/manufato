package com.example.manufato

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var currentPasswordInput: EditText
    private lateinit var newPasswordInput: EditText
    private lateinit var confirmNewPasswordInput: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        auth = FirebaseAuth.getInstance()

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

        val user = auth.currentUser

        if (user == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
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

        // Re-authenticate user before changing password
        val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)

        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update password
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                Log.d(TAG, "Password updated")
                                Toast.makeText(this, "Senha alterada com sucesso", Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                Log.e(TAG, "Error updating password", updateTask.exception)
                                Toast.makeText(this, "Erro ao atualizar senha: ${updateTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Log.e(TAG, "Error re-authenticating", task.exception)
                    currentPasswordInput.error = "Senha atual incorreta"
                    Toast.makeText(this, "Falha na autenticação", Toast.LENGTH_SHORT).show()
                }
            }
    }

    companion object {
        private const val TAG = "ChangePasswordActivity"
    }
}
