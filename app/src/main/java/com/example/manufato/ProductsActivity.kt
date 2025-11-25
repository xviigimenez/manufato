package com.example.manufato

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ProductsActivity : BaseActivity(), ProductsAdapter.OnProductClickListener {

    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout
    private lateinit var fabAddProduct: FrameLayout
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var dbHelper: DatabaseHelper

    override fun getCurrentNavItemId(): Int = R.id.nav_products

    private val addProductLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadProducts()
            Toast.makeText(this, "Produto salvo com sucesso", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize dbHelper before setContentView because super.onCreate might trigger layout inflation that doesn't use it yet,
        // but good practice to have it ready.
        // However, BaseActivity.setContentView calls setupBottomNavigation.
        // The crash is likely not here but let's be safe.
        dbHelper = DatabaseHelper(this)
        
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
        val products = dbHelper.getAllProducts()

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
        intent.putExtra("PRODUCT_ID", product.id.toString())
        addProductLauncher.launch(intent)
    }

    override fun onDeleteClick(product: Product) {
        dbHelper.deleteProduct(product.id)
        loadProducts()
        Toast.makeText(this, "Produto excluído", Toast.LENGTH_SHORT).show()
    }
}
