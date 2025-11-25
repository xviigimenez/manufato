package com.example.manufato

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val quantity: Int = 0,
    val category: String = "",
    val isAvailable: Boolean = true,
    val sales: Int = 0,
    val imageUri: String? = null
) : Parcelable
