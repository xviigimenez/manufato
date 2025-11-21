package com.example.manufato

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val category: String = "",
    val imageUri: String? = null,
    val sales: Int = 0,
    val isAvailable: Boolean = true
) : Parcelable
