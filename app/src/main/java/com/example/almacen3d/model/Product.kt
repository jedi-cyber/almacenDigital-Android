package com.example.almacen3d.model

data class Product(
    val sku: String,
    val name: String,
    val category: String,
    val brand: String,
    val shelfId: String,
    val width: Double,
    val height: Double,
    val depth: Double,
    val categoryId: Int? = null,
    val brandId: Int? = null,
    val localX: Double = 0.0,
    val localY: Double = 0.0,
    val localZ: Double = 0.0
)
