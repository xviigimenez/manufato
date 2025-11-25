package com.example.manufato

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductsActivity : BaseActivity(), ProductsAdapter.OnProductClickListener {

    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var fabAddProduct: FrameLayout
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun getCurrentNavItemId(): Int = R.id.nav_products

    private val addProductLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadProducts()
            // Toast handled in AddEditProductActivity usually, or here if needed
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        dbHelper = DatabaseHelper(this)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        initViews()
        setupRecyclerView()
        setupListeners()
        loadProducts()
    }

    private fun initViews() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
        emptyState = findViewById(R.id.emptyState)
        fabAddProduct = findViewById(R.id.fabAddProduct)
    }

    private fun setupRecyclerView() {
        productsAdapter = ProductsAdapter(mutableListOf(), this)
        productsRecyclerView.layoutManager = LinearLayoutManager(this)
        productsRecyclerView.adapter = productsAdapter
    }

    private fun setupListeners() {
        fabAddProduct.setOnClickListener {
            val intent = Intent(this, AddEditProductActivity::class.java)
            addProductLauncher.launch(intent)
        }
    }

    private fun loadProducts() {
        val user = auth.currentUser
        if (user != null) {
            // Load from Firestore for the current user
            db.collection("produtos")
                .whereEqualTo("artesao", user.uid)
                .get()
                .addOnSuccessListener { documents ->
                    val productList = mutableListOf<Product>()
                    for (document in documents) {
                        try {
                            val name = document.getString("nome") ?: ""
                            val description = document.getString("descricao") ?: ""
                            val price = document.getDouble("preco") ?: 0.0
                            val quantity = document.getLong("quantia")?.toInt() ?: 0
                            val category = document.getString("categoria") ?: ""
                            // Note: Firestore doesn't store image URI in our current implementation, so it might be null
                            // Or we map it if we decide to store it.
                            
                            val product = Product(
                                firestoreId = document.id,
                                name = name,
                                description = description,
                                price = price,
                                quantity = quantity,
                                category = category,
                                artesao = user.uid
                            )
                            productList.add(product)
                        } catch (e: Exception) {
                            Log.e("ProductsActivity", "Error parsing product", e)
                        }
                    }
                    
                    updateUI(productList)
                }
                .addOnFailureListener { exception ->
                    Log.w("ProductsActivity", "Error getting documents: ", exception)
                    Toast.makeText(this, "Erro ao carregar produtos", Toast.LENGTH_SHORT).show()
                    // Fallback to local DB or show empty state
                    updateUI(emptyList())
                }
        } else {
            // If not logged in, show empty or local (legacy) products?
            // Let's show local for now or redirect to login
            updateUI(emptyList())
        }
    }
    
    private fun updateUI(products: List<Product>) {
        if (products.isEmpty()) {
            productsRecyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            productsRecyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            productsAdapter.updateData(products)
        }
    }

    override fun onEditClick(product: Product) {
        val intent = Intent(this, AddEditProductActivity::class.java)
        intent.putExtra("PRODUCT_ID", product.id.toString()) // Passing local ID if available
        // Ideally we should pass the Firestore ID or the whole object
        // Since our Adapter holds Product objects which now have firestoreId, let's rely on that or pass data
        // For now keeping existing flow but note that AddEditProductActivity needs to handle loading via Firestore ID if we switch fully
        addProductLauncher.launch(intent)
    }

    override fun onDeleteClick(product: Product) {
        // Delete from Firestore
        if (product.firestoreId.isNotEmpty()) {
            db.collection("produtos").document(product.firestoreId)
                .delete()
                .addOnSuccessListener {
                    loadProducts()
                    Toast.makeText(this, "Produto excluído", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao excluir: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Fallback local delete
            dbHelper.deleteProduct(product.id)
            loadProducts()
        }
    }
}
