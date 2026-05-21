package com.example.almacen3d.ui

import android.content.res.Resources

fun Int.dp(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Double.cleanText(): String = if (this % 1.0 == 0.0) this.toInt().toString() else "%.2f".format(this)
