package com.example.manufato

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class EditProfileActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageView
    private lateinit var profilePhoto: ImageView
    private lateinit var defaultIcon: TextView
    private lateinit var cameraIconContainer: FrameLayout
    private lateinit var changePhotoText: TextView
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var changePasswordItem: LinearLayout
    private lateinit var privacyPolicyItem: LinearLayout
    private lateinit var deleteAccountItem: LinearLayout
    private lateinit var saveButton: Button
    private lateinit var cancelButton: TextView
    private lateinit var userPrefs: UserPreferences
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    
    private var currentPhotoUri: Uri? = null
    
    // Activity result launchers
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                saveImageToInternalStorage(uri)
            }
        }
    }
    
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoUri?.let { uri ->
                saveImageToInternalStorage(uri)
            }
        }
    }
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showPhotoSourceDialog()
        } else {
            Toast.makeText(this, "Permissão necessária para acessar fotos", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance()
        
        userPrefs = UserPreferences(this)
        
        initViews()
        setupListeners()
        loadUserData()
    }
    
    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        profilePhoto = findViewById(R.id.profilePhoto)
        defaultIcon = findViewById(R.id.defaultIcon)
        cameraIconContainer = findViewById(R.id.cameraIconContainer)
        changePhotoText = findViewById(R.id.changePhotoText)
        nameInput = findViewById(R.id.nameInput)
        emailInput = findViewById(R.id.emailInput)
        phoneInput = findViewById(R.id.phoneInput)
        changePasswordItem = findViewById(R.id.changePasswordItem)
        privacyPolicyItem = findViewById(R.id.privacyPolicyItem)
        deleteAccountItem = findViewById(R.id.deleteAccountItem)
        saveButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
    }
    
    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
        }
        
        cameraIconContainer.setOnClickListener {
            requestPhotoChange()
        }
        
        changePhotoText.setOnClickListener {
            requestPhotoChange()
        }

        changePasswordItem.setOnClickListener {
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }

        privacyPolicyItem.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        deleteAccountItem.setOnClickListener {
            showDeleteAccountDialog()
        }
        
        saveButton.setOnClickListener {
            saveChanges()
        }
        
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun showDeleteAccountDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Excluir conta")
        builder.setMessage("Para excluir sua conta, precisamos confirmar sua identidade. Digite sua senha:")

        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Excluir") { _, _ ->
            val password = input.text.toString()
            if (password.isNotEmpty()) {
                reauthenticateAndDelete(password)
            } else {
                Toast.makeText(this, "Senha não pode estar vazia", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun reauthenticateAndDelete(password: String) {
        val user = auth.currentUser
        val credential = EmailAuthProvider.getCredential(user?.email!!, password)

        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    deleteAccount()
                } else {
                    Toast.makeText(this, "Autenticação falhou. Senha incorreta.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun deleteAccount() {
        val user = auth.currentUser
        
        if (user != null) {
            // Delete from Firestore first
            db.collection("usuarios").document(user.uid)
                .delete()
                .addOnSuccessListener {
                    // Then delete auth account
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "User account deleted.")
                                userPrefs.clearAllData()
                                Toast.makeText(this@EditProfileActivity, "Conta excluída com sucesso", Toast.LENGTH_SHORT).show()
                                
                                // Navigate back to login
                                val intent = Intent(this@EditProfileActivity, LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()
                            } else {
                                Log.e(TAG, "User account deletion failed.", task.exception)
                                Toast.makeText(this@EditProfileActivity, "Erro ao excluir conta: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting document", e)
                    Toast.makeText(this, "Erro ao excluir dados: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    
    private fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            nameInput.setText(user.displayName ?: "")
            emailInput.setText(user.email ?: "")
            
            // Load additional data from Firestore
            db.collection("usuarios").document(user.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val phone = document.getString("telefone")
                        if (!phone.isNullOrEmpty()) {
                            phoneInput.setText(phone)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        } else {
            nameInput.setText(userPrefs.getUserName() ?: "")
            emailInput.setText(userPrefs.getUserEmail() ?: "")
        }
        
        // Load profile image (locally stored for now)
        userPrefs.getProfileImageUri()?.let { uriString ->
            try {
                val uri = Uri.parse(uriString)
                val file = File(uri.path ?: return@let)
                if (file.exists()) {
                    profilePhoto.setImageURI(uri)
                    profilePhoto.visibility = View.VISIBLE
                    defaultIcon.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun requestPhotoChange() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            showPhotoSourceDialog()
        }
    }
    
    private fun showPhotoSourceDialog() {
        val options = arrayOf(
            getString(R.string.gallery),
            getString(R.string.camera)
        )
        
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.choose_photo_source))
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> openCamera()
                }
            }
            .show()
    }
    
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }
    
    private fun openCamera() {
        val photoFile = File(filesDir, "profile_photo_${System.currentTimeMillis()}.jpg")
        currentPhotoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
        cameraLauncher.launch(intent)
    }
    
    private fun saveImageToInternalStorage(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            // Create a file in internal storage
            val file = File(filesDir, "profile_photo.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
            
            // Save URI to preferences
            val savedUri = Uri.fromFile(file)
            userPrefs.setProfileImageUri(savedUri.toString())
            
            // Update UI
            profilePhoto.setImageBitmap(bitmap)
            profilePhoto.visibility = View.VISIBLE
            defaultIcon.visibility = View.GONE
            
            Toast.makeText(this, "Foto atualizada", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro ao salvar foto", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun saveChanges() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val phone = phoneInput.text.toString().trim()
        
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
        
        val user = auth.currentUser
        
        if (user != null) {
            // Update Profile Name in Auth
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()

            user.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                        
                        // Update Firestore data
                        val userUpdates = hashMapOf<String, Any>(
                            "nome" to name,
                            "telefone" to phone
                            // Note: email is updated via updateEmail logic below, not here directly to avoid inconsistency
                        )
                        
                        db.collection("usuarios").document(user.uid)
                            .update(userUpdates)
                            .addOnSuccessListener {
                                Log.d(TAG, "Firestore updated")
                                
                                // Check if email changed
                                if (email != user.email) {
                                    showEmailUpdateDialog(email)
                                } else {
                                    showToastAndFinish("Perfil atualizado com sucesso!")
                                }
                            }
                            .addOnFailureListener { e ->
                                showToast("Erro ao atualizar dados: ${e.message}")
                            }

                    } else {
                        showToast("Erro ao atualizar perfil: ${task.exception?.message}")
                    }
                }
        }
    }
    
    private fun showEmailUpdateDialog(newEmail: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atualizar Email")
        builder.setMessage("Para atualizar seu email, precisamos confirmar sua identidade. Digite sua senha:")

        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
        builder.setView(input)

        builder.setPositiveButton("Confirmar") { _, _ ->
            val password = input.text.toString()
            if (password.isNotEmpty()) {
                reauthenticateAndUpdateEmail(password, newEmail)
            } else {
                Toast.makeText(this, "Senha não pode estar vazia", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }
    
    private fun reauthenticateAndUpdateEmail(password: String, newEmail: String) {
        val user = auth.currentUser
        val credential = EmailAuthProvider.getCredential(user?.email!!, password)

        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    updateEmail(newEmail)
                } else {
                    Toast.makeText(this, "Autenticação falhou. Senha incorreta.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    
    private fun updateEmail(newEmail: String) {
        val user = auth.currentUser
        user?.verifyBeforeUpdateEmail(newEmail)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User email update verification sent.")
                    
                    // Also update email in Firestore for consistency
                    db.collection("usuarios").document(user.uid)
                        .update("email", newEmail)
                    
                    showToastAndFinish("Email de verificação enviado. Verifique o novo email.")
                } else {
                    Log.e(TAG, "User email update failed.", task.exception)
                    showToast("Erro ao atualizar email: ${task.exception?.message}")
                }
            }
    }
    
    private fun showToastAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    }
    
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "EditProfileActivity"
    }
}
