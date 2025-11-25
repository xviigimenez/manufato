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
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

class AddEditProductActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageView
    private lateinit var pageTitle: TextView
    private lateinit var imageContainer: FrameLayout
    private lateinit var productImagePreview: ImageView
    private lateinit var addImageText: TextView
    private lateinit var productNameInput: EditText
    private lateinit var productDescriptionInput: EditText
    private lateinit var productPriceInput: EditText
    private lateinit var productQuantityInput: EditText
    private lateinit var productCategoryInput: EditText
    private lateinit var availabilitySwitch: SwitchCompat
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button
    
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    
    private var currentProduct: Product? = null
    private var productImageUri: String? = null
    private var currentPhotoUri: Uri? = null
    
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                saveProductImage(uri)
            }
        }
    }
    
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            currentPhotoUri?.let { uri ->
                saveProductImage(uri)
            }
        }
    }
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showImageSourceDialog()
        } else {
            Toast.makeText(this, "Permissão necessária para acessar fotos", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_product)
        
        dbHelper = DatabaseHelper(this)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        
        val productIdString = intent.getStringExtra("PRODUCT_ID")
        
        if (productIdString != null) {
            // Try to load from SQLite (legacy) or intent data if passed directly
            // Ideally, we should fetch from Firestore if we have an ID
            // For now, let's keep the local DB fetch for compatibility but we should transition to Firestore fetch
            try {
                val productId = productIdString.toLong()
                currentProduct = dbHelper.getProduct(productId)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
        }
        
        initViews()
        setupListeners()
        loadProductData()
    }
    
    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        pageTitle = findViewById(R.id.pageTitle)
        imageContainer = findViewById(R.id.imageContainer)
        productImagePreview = findViewById(R.id.productImagePreview)
        addImageText = findViewById(R.id.addImageText)
        productNameInput = findViewById(R.id.productNameInput)
        productDescriptionInput = findViewById(R.id.productDescriptionInput)
        productPriceInput = findViewById(R.id.productPriceInput)
        productQuantityInput = findViewById(R.id.productQuantityInput)
        productCategoryInput = findViewById(R.id.productCategoryInput)
        availabilitySwitch = findViewById(R.id.availabilitySwitch)
        saveButton = findViewById(R.id.saveButton)
        deleteButton = findViewById(R.id.deleteButton)
    }
    
    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
        }
        
        imageContainer.setOnClickListener {
            requestImageSelection()
        }
        
        saveButton.setOnClickListener {
            saveProduct()
        }
        
        deleteButton.setOnClickListener {
            showDeleteConfirmation()
        }
    }
    
    private fun loadProductData() {
        currentProduct?.let { product ->
            pageTitle.text = getString(R.string.edit_product)
            deleteButton.visibility = View.VISIBLE
            
            productNameInput.setText(product.name)
            productDescriptionInput.setText(product.description)
            productPriceInput.setText(product.price.toString())
            productQuantityInput.setText(product.quantity.toString())
            productCategoryInput.setText(product.category)
            availabilitySwitch.isChecked = product.isAvailable
            
            product.imageUri?.let { uriString ->
                productImageUri = uriString
                loadImage(uriString)
            }
        }
    }
    
    private fun loadImage(uriString: String) {
        try {
            val uri = Uri.parse(uriString)
            val file = File(uri.path ?: return)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.path)
                productImagePreview.setImageBitmap(bitmap)
                addImageText.visibility = View.GONE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun requestImageSelection() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            showImageSourceDialog()
        }
    }
    
    private fun showImageSourceDialog() {
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
        val photoFile = File(filesDir, "product_${System.currentTimeMillis()}.jpg")
        currentPhotoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
        cameraLauncher.launch(intent)
    }
    
    private fun saveProductImage(uri: Uri) {
        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            
            val file = File(filesDir, "product_${UUID.randomUUID()}.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
            
            productImageUri = Uri.fromFile(file).toString()
            productImagePreview.setImageBitmap(bitmap)
            addImageText.visibility = View.GONE
            
            Toast.makeText(this, "Imagem adicionada", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erro ao salvar imagem", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun saveProduct() {
        val name = productNameInput.text.toString().trim()
        val description = productDescriptionInput.text.toString().trim()
        val priceStr = productPriceInput.text.toString().trim()
        val quantityStr = productQuantityInput.text.toString().trim()
        val category = productCategoryInput.text.toString().trim()
        val isAvailable = availabilitySwitch.isChecked
        
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Você precisa estar logado para salvar produtos.", Toast.LENGTH_SHORT).show()
            return
        }
        
        // Validation
        if (name.isEmpty()) {
            productNameInput.error = "Nome é obrigatório"
            productNameInput.requestFocus()
            return
        }
        
        if (description.isEmpty()) {
            productDescriptionInput.error = "Descrição é obrigatória"
            productDescriptionInput.requestFocus()
            return
        }
        
        if (priceStr.isEmpty()) {
            productPriceInput.error = "Preço é obrigatório"
            productPriceInput.requestFocus()
            return
        }
        
        val price = priceStr.toDoubleOrNull()
        if (price == null || price < 0) {
            productPriceInput.error = "Preço inválido"
            productPriceInput.requestFocus()
            return
        }
        
        if (quantityStr.isEmpty()) {
            productQuantityInput.error = "Quantidade é obrigatória"
            productQuantityInput.requestFocus()
            return
        }
        
        val quantity = quantityStr.toIntOrNull()
        if (quantity == null || quantity < 0) {
            productQuantityInput.error = "Quantidade inválida"
            productQuantityInput.requestFocus()
            return
        }
        
        if (category.isEmpty()) {
            productCategoryInput.error = "Categoria é obrigatória"
            productCategoryInput.requestFocus()
            return
        }
        
        val productMap = hashMapOf(
            "nome" to name,
            "descricao" to description,
            "preco" to price,
            "quantia" to quantity,
            "artesao" to user.uid,
            "categoria" to category,
            "disponivel" to isAvailable,
            // Note: We're not saving the image URI to Firestore for now as requested by parameters
            // Ideally we would upload image to Firebase Storage and save the URL here
        )
        
        // Create or update product locally and in Firebase
        if (currentProduct != null) {
            // Update existing
            val product = Product(
                id = currentProduct!!.id,
                firestoreId = currentProduct!!.firestoreId,
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                category = category,
                isAvailable = isAvailable,
                sales = currentProduct!!.sales,
                imageUri = productImageUri,
                artesao = user.uid
            )
            
            dbHelper.updateProduct(product)
            
            // Also update in Firestore if we have a firestoreId, or create a new one?
            // Ideally we would have linked the local ID to a firestore ID.
            // For this example, let's assume we are just adding new ones to Firestore or updating if we knew the ID.
             if (currentProduct!!.firestoreId.isNotEmpty()) {
                 db.collection("produtos").document(currentProduct!!.firestoreId)
                     .update(productMap as Map<String, Any>)
             }

            Toast.makeText(this, "Produto atualizado!", Toast.LENGTH_SHORT).show()
        } else {
            // Add new
            val product = Product(
                name = name,
                description = description,
                price = price,
                quantity = quantity,
                category = category,
                isAvailable = isAvailable,
                sales = 0,
                imageUri = productImageUri,
                artesao = user.uid
            )
            
            val id = dbHelper.addProduct(product)
            
            // Add to Firestore
            db.collection("produtos")
                .add(productMap)
                .addOnSuccessListener { documentReference ->
                    // Optionally update local DB with Firestore ID if needed
                    Toast.makeText(this, "Produto salvo no Firebase!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao salvar no Firebase: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            
            Toast.makeText(this, "Produto adicionado!", Toast.LENGTH_SHORT).show()
        }
        
        setResult(Activity.RESULT_OK)
        finish()
    }
    
    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Excluir Produto")
            .setMessage("Tem certeza que deseja excluir este produto?")
            .setPositiveButton("Excluir") { _, _ ->
                deleteProduct()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    
    private fun deleteProduct() {
        currentProduct?.let { product ->
            dbHelper.deleteProduct(product.id)
            
            // Also delete from Firestore if it exists there
             if (product.firestoreId.isNotEmpty()) {
                 db.collection("produtos").document(product.firestoreId).delete()
             }
            
            Toast.makeText(this, "Produto excluído", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}
