package com.example.manufato

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ProductPreferences(context: Context) {
    
    private val prefs: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val PREF_NAME = "manufato_products_prefs"
        private const val KEY_PRODUCTS = "products"
    }
    
    fun saveProducts(products: List<Product>) {
        val json = gson.toJson(products)
        prefs.edit().putString(KEY_PRODUCTS, json).apply()
    }
    
    fun getProducts(): List<Product> {
        val json = prefs.getString(KEY_PRODUCTS, null) ?: return emptyList()
        val type = object : TypeToken<List<Product>>() {}.type
        return gson.fromJson(json, type)
    }
    
    fun addProduct(product: Product) {
        val products = getProducts().toMutableList()
        products.add(product)
        saveProducts(products)
    }
    
    fun updateProduct(updatedProduct: Product) {
        val products = getProducts().toMutableList()
        val index = products.indexOfFirst { it.id == updatedProduct.id }
        if (index != -1) {
            products[index] = updatedProduct
            saveProducts(products)
        }
    }
    
    fun deleteProduct(productId: String) {
        val products = getProducts().toMutableList()
        products.removeAll { it.id == productId }
        saveProducts(products)
    }
    
    fun getProductById(id: String): Product? {
        return getProducts().find { it.id == id }
    }
}
