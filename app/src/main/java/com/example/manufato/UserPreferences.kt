package com.example.manufato

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREF_NAME = "manufato_user_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PASSWORD = "user_password"
    }
    
    fun saveUser(name: String, email: String, password: String) {
        prefs.edit().apply {
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_PASSWORD, password)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun login(email: String, password: String): Boolean {
        val storedEmail = prefs.getString(KEY_USER_EMAIL, null)
        val storedPassword = prefs.getString(KEY_USER_PASSWORD, null)
        
        return if (email == storedEmail && password == storedPassword) {
            prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply()
            true
        } else {
            false
        }
    }
    
    fun logout() {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }
    
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }
    
    fun isUserRegistered(): Boolean {
        return prefs.getString(KEY_USER_EMAIL, null) != null
    }
    
    fun updateUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }
    
    fun updateUserEmail(email: String) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()
    }
    
    fun clearAllData() {
        prefs.edit().clear().apply()
    }
}
