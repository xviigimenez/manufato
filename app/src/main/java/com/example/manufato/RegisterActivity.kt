package com.example.manufato

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var registerButton: Button
    private lateinit var loginLink: TextView
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        initViews()
        setupListeners()
    }
    
    private fun initViews() {
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        registerButton = findViewById(R.id.registerButton)
        loginLink = findViewById(R.id.loginLink)
    }
    
    private fun setupListeners() {
        registerButton.setOnClickListener {
            performRegister()
        }
        
        loginLink.setOnClickListener {
            navigateToLogin()
        }
    }
    
    private fun performRegister() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        val confirmPassword = confirmPasswordInput.text.toString().trim()
        
        // Validation
        if (name.isEmpty()) {
            nameInput.error = "Nome é obrigatório"
            nameInput.requestFocus()
            return
        }
        
        if (name.length < 3) {
            nameInput.error = "Nome deve ter no mínimo 3 caracteres"
            nameInput.requestFocus()
            return
        }
        
        if (email.isEmpty()) {
            emailInput.error = "Email é obrigatório"
            emailInput.requestFocus()
            return
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.error = "Email inválido"
            emailInput.requestFocus()
            return
        }
        
        if (password.isEmpty()) {
            passwordInput.error = "Senha é obrigatória"
            passwordInput.requestFocus()
            return
        }
        
        if (password.length < 6) {
            passwordInput.error = "Senha deve ter no mínimo 6 caracteres"
            passwordInput.requestFocus()
            return
        }
        
        if (confirmPassword.isEmpty()) {
            confirmPasswordInput.error = "Confirmação de senha é obrigatória"
            confirmPasswordInput.requestFocus()
            return
        }
        
        if (password != confirmPassword) {
            confirmPasswordInput.error = "As senhas não coincidem"
            confirmPasswordInput.requestFocus()
            return
        }
        
        // Create user with Firebase
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    
                    // Update user profile with name
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { profileTask ->
                            if (profileTask.isSuccessful) {
                                Log.d(TAG, "User profile updated.")
                                showToast("Cadastro realizado com sucesso!")
                                navigateToMain()
                            }
                        }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    showToast("Falha no cadastro: ${task.exception?.localizedMessage}")
                }
            }
    }
    
    private fun navigateToLogin() {
        finish()
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    companion object {
        private const val TAG = "RegisterActivity"
    }
}
