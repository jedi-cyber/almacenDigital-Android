package com.example.almacen3d.model

data class Shelf(
    val id: String,
    val label: String,
    val sections: Int,
    val width: Double,
    val height: Double,
    val depth: Double,
    val rotationY: Double
)
