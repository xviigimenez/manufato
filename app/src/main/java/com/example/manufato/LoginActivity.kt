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

class LoginActivity : AppCompatActivity() {
    
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var forgotPassword: TextView
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        
        // Check if user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToMain()
            return
        }
        
        setContentView(R.layout.activity_login)
        
        initViews()
        setupListeners()
    }
    
    private fun initViews() {
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerLink = findViewById(R.id.registerLink)
        forgotPassword = findViewById(R.id.forgotPassword)
    }
    
    private fun setupListeners() {
        loginButton.setOnClickListener {
            performLogin()
        }
        
        registerLink.setOnClickListener {
            navigateToRegister()
        }
        
        forgotPassword.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (email.isNotEmpty()) {
                sendPasswordResetEmail(email)
            } else {
                emailInput.error = "Digite seu e-mail para recuperar a senha"
                emailInput.requestFocus()
            }
        }
    }
    
    private fun performLogin() {
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        
        // Validation
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
        
        // Attempt login with Firebase
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    showToast("Login realizado com sucesso!")
                    navigateToMain()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    showToast("Falha na autenticação: ${task.exception?.localizedMessage}")
                }
            }
    }
    
    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    showToast("Email de recuperação de senha enviado.")
                } else {
                    showToast("Falha ao enviar email: ${task.exception?.localizedMessage}")
                }
            }
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    
    companion object {
        private const val TAG = "LoginActivity"
    }
}
