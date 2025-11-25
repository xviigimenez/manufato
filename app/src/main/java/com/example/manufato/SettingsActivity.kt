package com.example.manufato

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var notificationsSwitch: SwitchMaterial
    private lateinit var darkModeSwitch: SwitchMaterial
    private lateinit var changePasswordItem: LinearLayout
    private lateinit var privacyPolicyItem: LinearLayout
    private lateinit var deleteAccountItem: LinearLayout
    private lateinit var settingsPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        settingsPrefs = getSharedPreferences("ManufatoSettings", MODE_PRIVATE)
        
        initViews()
        loadSettings()
        setupListeners()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        notificationsSwitch = findViewById(R.id.notificationsSwitch)
        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        changePasswordItem = findViewById(R.id.changePasswordItem)
        privacyPolicyItem = findViewById(R.id.privacyPolicyItem)
        deleteAccountItem = findViewById(R.id.deleteAccountItem)
    }

    private fun loadSettings() {
        // Load saved settings
        val notificationsEnabled = settingsPrefs.getBoolean("notifications_enabled", true)
        notificationsSwitch.isChecked = notificationsEnabled

        val isDarkMode = settingsPrefs.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkMode
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
        }

        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsPrefs.edit().putBoolean("notifications_enabled", isChecked).apply()
            val status = if (isChecked) "ativadas" else "desativadas"
            Toast.makeText(this, "Notificações $status", Toast.LENGTH_SHORT).show()
        }

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            settingsPrefs.edit().putBoolean("dark_mode", isChecked).apply()
            
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        changePasswordItem.setOnClickListener {
            Toast.makeText(this, "Alterar senha em desenvolvimento", Toast.LENGTH_SHORT).show()
        }

        privacyPolicyItem.setOnClickListener {
            Toast.makeText(this, "Política de privacidade em desenvolvimento", Toast.LENGTH_SHORT).show()
        }

        deleteAccountItem.setOnClickListener {
            Toast.makeText(this, "Funcionalidade de excluir conta em desenvolvimento", Toast.LENGTH_SHORT).show()
        }
    }
}
