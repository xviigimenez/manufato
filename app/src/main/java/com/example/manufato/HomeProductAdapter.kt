package com.example.manufato

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class HomeProductAdapter(
    private var products: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<HomeProductAdapter.ProductViewHolder>() {

    private val auth = FirebaseAuth.getInstance()

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val userProductIcon: ImageView = itemView.findViewById(R.id.userProductIcon)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productDescription: TextView = itemView.findViewById(R.id.productDescription)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val imagePlaceholder: View = itemView.findViewById(R.id.imagePlaceholder)

        fun bind(product: Product) {
            productName.text = product.name
            productDescription.text = product.description
            productPrice.text = "R$ %.2f".format(product.price)

            // Check if product belongs to current user
            val currentUser = auth.currentUser
            if (currentUser != null && product.artesao == currentUser.uid) {
                userProductIcon.visibility = View.VISIBLE
            } else {
                userProductIcon.visibility = View.GONE
            }

            // Load product image
            product.imageUri?.let { uriString ->
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
            } ?: run {
                productImage.visibility = View.GONE
                imagePlaceholder.visibility = View.VISIBLE
            }

            itemView.setOnClickListener {
                onProductClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
