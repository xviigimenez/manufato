package com.example.manufato

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ProductDetailsActivity : AppCompatActivity() {
    
    private lateinit var backButton: ImageView
    private lateinit var productImage: ImageView
    private lateinit var imagePlaceholder: View
    private lateinit var productCategory: TextView
    private lateinit var productName: TextView
    private lateinit var productPrice: TextView
    private lateinit var productDescription: TextView
    private lateinit var productQuantity: TextView
    private lateinit var addToCartButton: Button
    private lateinit var buyNowButton: Button
    
    private var product: Product? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        
        product = intent.getParcelableExtra("PRODUCT")
        
        initViews()
        setupListeners()
        loadProductData()
    }
    
    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        productImage = findViewById(R.id.productImage)
        imagePlaceholder = findViewById(R.id.imagePlaceholder)
        productCategory = findViewById(R.id.productCategory)
        productName = findViewById(R.id.productName)
        productPrice = findViewById(R.id.productPrice)
        productDescription = findViewById(R.id.productDescription)
        productQuantity = findViewById(R.id.productQuantity)
        addToCartButton = findViewById(R.id.addToCartButton)
        buyNowButton = findViewById(R.id.buyNowButton)
    }
    
    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
        }
        
        addToCartButton.setOnClickListener {
            addToCart()
        }
        
        buyNowButton.setOnClickListener {
            buyNow()
        }
    }
    
    private fun loadProductData() {
        product?.let { p ->
            productCategory.text = p.category
            productName.text = p.name
            productDescription.text = p.description
            productPrice.text = String.format("R$ %.2f", p.price)
            productQuantity.text = "${p.quantity} unidades disponíveis"
            
            // Load image
            p.imageUri?.let { uriString ->
                try {
                    val uri = Uri.parse(uriString)
                    val file = File(uri.path ?: return@let)
                    if (file.exists()) {
                        productImage.setImageURI(uri)
                        productImage.visibility = View.VISIBLE
                        imagePlaceholder.visibility = View.GONE
                    } else {
                        productImage.visibility = View.GONE
                        imagePlaceholder.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    productImage.visibility = View.GONE
                    imagePlaceholder.visibility = View.VISIBLE
                }
            }
        } ?: run {
            Toast.makeText(this, "Erro ao carregar produto", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun addToCart() {
        Toast.makeText(this, "Adicionado ao carrinho (Em desenvolvimento)", Toast.LENGTH_SHORT).show()
        // Logic to add to cart would go here
    }
    
    private fun buyNow() {
        Toast.makeText(this, "Comprar agora (Em desenvolvimento)", Toast.LENGTH_SHORT).show()
        // Logic for direct checkout would go here
    }
}
