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
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
    private lateinit var saveButton: Button
    private lateinit var cancelButton: TextView
    private lateinit var userPrefs: UserPreferences
    
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
        
        saveButton.setOnClickListener {
            saveChanges()
        }
        
        cancelButton.setOnClickListener {
            finish()
        }
    }
    
    private fun loadUserData() {
        nameInput.setText(userPrefs.getUserName() ?: "")
        emailInput.setText(userPrefs.getUserEmail() ?: "")
        
        // Load profile image
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
        
        // Save changes
        userPrefs.updateUserName(name)
        userPrefs.updateUserEmail(email)
        
        Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
        
        // Return to profile
        setResult(Activity.RESULT_OK)
        finish()
    }
}
