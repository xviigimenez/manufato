package com.example.manufato

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ProductAdapter(
    private var products: List<Product>,
    private val onEditClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.productImage)
        val defaultProductIcon: TextView = view.findViewById(R.id.defaultProductIcon)
        val productName: TextView = view.findViewById(R.id.productName)
        val productCategory: TextView = view.findViewById(R.id.productCategory)
        val productPrice: TextView = view.findViewById(R.id.productPrice)
        val productStock: TextView = view.findViewById(R.id.productStock)
        val productSales: TextView = view.findViewById(R.id.productSales)
        val productAvailability: TextView = view.findViewById(R.id.productAvailability)
        val editButton: ImageView = view.findViewById(R.id.editButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        
        holder.productName.text = product.name
        holder.productCategory.text = product.category
        holder.productPrice.text = String.format("R$ %.2f", product.price)
        holder.productStock.text = product.quantity.toString()
        holder.productSales.text = product.sales.toString()
        
        // Set availability
        if (product.isAvailable) {
            holder.productAvailability.text = holder.itemView.context.getString(R.string.available)
            holder.productAvailability.setTextColor(
                holder.itemView.context.getColor(R.color.primary)
            )
        } else {
            holder.productAvailability.text = holder.itemView.context.getString(R.string.unavailable)
            holder.productAvailability.setTextColor(
                holder.itemView.context.getColor(R.color.text_secondary)
            )
        }
        
        // Load product image
        product.imageUri?.let { uriString ->
            try {
                val uri = Uri.parse(uriString)
                val file = File(uri.path ?: return@let)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.path)
                    holder.productImage.setImageBitmap(bitmap)
                    holder.productImage.visibility = View.VISIBLE
                    holder.defaultProductIcon.visibility = View.GONE
                } else {
                    holder.productImage.visibility = View.GONE
                    holder.defaultProductIcon.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                holder.productImage.visibility = View.GONE
                holder.defaultProductIcon.visibility = View.VISIBLE
            }
        } ?: run {
            holder.productImage.visibility = View.GONE
            holder.defaultProductIcon.visibility = View.VISIBLE
        }
        
        holder.editButton.setOnClickListener {
            onEditClick(product)
        }
    }

    override fun getItemCount() = products.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}
