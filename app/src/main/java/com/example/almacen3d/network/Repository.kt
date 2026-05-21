package com.example.almacen3d.network

import com.example.almacen3d.model.Product
import com.example.almacen3d.model.Shelf
import com.example.almacen3d.model.ActiveSession
import com.example.almacen3d.model.UserSession

class WarehouseRepository(private val apiClient: WarehouseApiClient) {
    fun authenticate(email: String, password: String): Result<UserSession> = runCatching { apiClient.authenticate(email, password) }
    fun fetchProducts(): Result<List<Product>> = runCatching { apiClient.fetchProducts() }
    fun fetchShelves(): Result<List<Shelf>> = runCatching { apiClient.fetchShelves() }
    fun saveProduct(product: Product): Result<Unit> = runCatching { apiClient.saveProduct(product) }
    fun currentSession(): Result<UserSession> = runCatching { apiClient.currentSession() }
    fun fetchActiveSessions(): Result<List<ActiveSession>> = runCatching { apiClient.fetchActiveSessions() }
    fun updateProfile(name: String, email: String): Result<UserSession> = runCatching { apiClient.updateProfile(name, email) }
    fun logout(allDevices: Boolean): Result<Unit> = runCatching { apiClient.logout(allDevices) }
}
