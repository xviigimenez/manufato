package com.example.manufato

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : BaseActivity() {
    
    private lateinit var searchInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var homeProductAdapter: HomeProductAdapter
    private lateinit var db: FirebaseFirestore
    
    private var allProducts: List<Product> = emptyList()
    private var currentSearchText: String = ""
    private var currentCategory: String = ""
    
    override fun getCurrentNavItemId(): Int = R.id.nav_home
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        db = FirebaseFirestore.getInstance()
        
        initViews()
        setupProductsRecyclerView()
        setupSearchListener()
        loadAllProducts()
    }
    
    override fun onResume() {
        super.onResume()
        loadAllProducts()
    }
    
    private fun initViews() {
        searchInput = findViewById(R.id.searchInput)
        categorySpinner = findViewById(R.id.categorySpinner)
        productsRecyclerView = findViewById(R.id.productsRecyclerView)
    }
    
    private fun setupCategorySpinner(availableCategories: List<String>) {
        val allCategories = mutableListOf(getString(R.string.categories_title))
        allCategories.addAll(availableCategories)
        
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, allCategories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter
        
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentCategory = if (position == 0) "" else allCategories[position]
                filterProducts()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }
    
    private fun setupProductsRecyclerView() {
        // Setup RecyclerView with a GridLayoutManager (2 columns)
        productsRecyclerView.layoutManager = GridLayoutManager(this, 2)
        
        homeProductAdapter = HomeProductAdapter(emptyList()) { product ->
            val intent = Intent(this, ProductDetailsActivity::class.java)
            intent.putExtra("PRODUCT", product)
            startActivity(intent)
        }
        productsRecyclerView.adapter = homeProductAdapter
    }
    
    private fun setupSearchListener() {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            
            override fun afterTextChanged(s: Editable?) {
                currentSearchText = s.toString().trim()
                filterProducts()
            }
        })
    }
    
    private fun loadAllProducts() {
        db.collection("produtos")
            .get()
            .addOnSuccessListener { documents ->
                val productList = mutableListOf<Product>()
                val categories = mutableSetOf<String>()
                
                for (document in documents) {
                    try {
                        val name = document.getString("nome") ?: ""
                        val description = document.getString("descricao") ?: ""
                        val price = document.getDouble("preco") ?: 0.0
                        val quantity = document.getLong("quantia")?.toInt() ?: 0
                        val category = document.getString("categoria") ?: ""
                        val artesao = document.getString("artesao") ?: ""
                        val disponivel = document.getBoolean("disponivel") ?: true

                        // Filter locally: check if available AND quantity > 0
                        if (disponivel && quantity > 0) {
                            val product = Product(
                                firestoreId = document.id,
                                name = name,
                                description = description,
                                price = price,
                                quantity = quantity,
                                category = category,
                                artesao = artesao,
                                isAvailable = disponivel
                            )
                            productList.add(product)
                            if (category.isNotEmpty()) {
                                categories.add(category)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("MainActivity", "Error parsing product", e)
                    }
                }
                
                allProducts = productList
                setupCategorySpinner(categories.toList().sorted())
                filterProducts()
                Log.d("MainActivity", "Loaded ${productList.size} products")
            }
            .addOnFailureListener { exception ->
                Log.w("MainActivity", "Error getting documents: ", exception)
                Toast.makeText(this, "Erro ao carregar produtos: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun filterProducts() {
        val filteredList = allProducts.filter { product ->
            val matchesSearch = product.name.contains(currentSearchText, ignoreCase = true) ||
                                product.description.contains(currentSearchText, ignoreCase = true)
            
            val matchesCategory = currentCategory.isEmpty() || product.category == currentCategory
            
            matchesSearch && matchesCategory
        }
        
        homeProductAdapter.updateProducts(filteredList)
    }
}
