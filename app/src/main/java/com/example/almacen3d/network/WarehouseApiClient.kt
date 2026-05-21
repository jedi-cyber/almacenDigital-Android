package com.example.almacen3d.network

import com.example.almacen3d.model.Product
import com.example.almacen3d.model.Shelf
import com.example.almacen3d.model.ActiveSession
import com.example.almacen3d.model.UserAccount
import com.example.almacen3d.model.UserSession
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class WarehouseApiClient(
    private var baseUrl: String = ApiConfig.DEFAULT_BASE_URL
) {
    private var sessionToken: String? = null
    private var sessionEmail: String = ""

    fun setBaseUrl(value: String) {
        baseUrl = value.trim().trimEnd('/') + "/"
        sessionToken = null
    }

    fun setSessionToken(token: String?) {
        sessionToken = token?.trim()?.takeIf { it.isNotBlank() }
    }

    fun authenticate(email: String, password: String): UserSession {
        sessionEmail = email.trim()
        sessionToken = null
        val body = JSONObject()
            .put("email", sessionEmail)
            .put("password", password)
        val response = requestRaw("POST", sessionUrl(), body.toString(), null)
        return parseSession(JSONObject(response).getJSONObject("session"))
    }

    fun hasSession(): Boolean = !sessionToken.isNullOrBlank()

    fun productsUrl(): String = "${baseUrl}productos.php"

    fun shelvesUrl(): String = "${baseUrl}config.php"

    private fun sessionUrl(): String = "${baseUrl}sesion.php"

    fun fetchProducts(): List<Product> {
        val json = JSONObject(request("GET", productsUrl()))
        val products = json.getJSONArray("products")
        return List(products.length()) { index ->
            val row = products.getJSONObject(index)
            val item = row.getJSONObject("item")
            val position = row.optJSONObject("localPosition")
            Product(
                sku = item.getString("sku"),
                name = item.optString("name", item.getString("sku")),
                category = item.optString("category", "Sin categoria"),
                brand = item.optString("brand", "Sin marca"),
                shelfId = row.getString("shelfId"),
                width = item.getDouble("width"),
                height = item.getDouble("height"),
                depth = item.getDouble("depth"),
                categoryId = if (item.isNull("categoryId")) null else item.optInt("categoryId"),
                brandId = if (item.isNull("brandId")) null else item.optInt("brandId"),
                localX = position?.optDouble("x", 0.0) ?: 0.0,
                localY = position?.optDouble("y", 0.0) ?: 0.0,
                localZ = position?.optDouble("z", 0.0) ?: 0.0
            )
        }
    }

    fun fetchShelves(): List<Shelf> {
        val json = JSONObject(request("GET", shelvesUrl()))
        val shelves = json.getJSONArray("shelves")
        return List(shelves.length()) { index ->
            val row = shelves.getJSONObject(index)
            Shelf(
                id = row.getString("id"),
                label = row.optString("label", row.getString("id")),
                sections = row.optInt("sections", 1),
                width = row.getDouble("width"),
                height = row.getDouble("height"),
                depth = row.getDouble("depth"),
                rotationY = row.optDouble("rotationY", 0.0)
            )
        }
    }

    fun saveProduct(product: Product) {
        val body = JSONObject()
            .put("sku", product.sku)
            .put("shelfId", product.shelfId)
            .put("name", product.name)
            .put("category", product.category)
            .put("brand", product.brand)
            .put("width", product.width)
            .put("height", product.height)
            .put("depth", product.depth)
            .put(
                "localPosition",
                JSONObject()
                    .put("x", product.localX)
                    .put("y", product.localY)
                    .put("z", product.localZ)
            )
        request("POST", productsUrl(), body.toString())
    }

    fun currentSession(): UserSession {
        val json = JSONObject(request("GET", sessionUrl()))
        return parseSession(json.getJSONObject("session"))
    }

    fun fetchActiveSessions(): List<ActiveSession> {
        val json = JSONObject(request("GET", "${sessionUrl()}?scope=active"))
        val rows = json.optJSONArray("sessions") ?: JSONArray()
        return List(rows.length()) { index ->
            val row = rows.getJSONObject(index)
            ActiveSession(
                id = row.optInt("id"),
                current = row.optBoolean("current"),
                ipAddress = row.optString("ipAddress", "Sin IP"),
                userAgent = row.optString("userAgent", "Dispositivo"),
                lastSeenAt = row.optString("lastSeenAt", ""),
                expiresAt = row.optString("expiresAt", "")
            )
        }
    }

    fun updateProfile(name: String, email: String, currentPassword: String = "", newPassword: String = ""): UserSession {
        val body = JSONObject()
            .put("name", name)
            .put("email", email)
            .put("currentPassword", currentPassword)
            .put("newPassword", newPassword)
        val json = JSONObject(request("PATCH", sessionUrl(), body.toString()))
        return parseSession(json.getJSONObject("session"))
    }

    fun logout(allDevices: Boolean = false) {
        val suffix = if (allDevices) "?scope=all" else ""
        runCatching { request("DELETE", sessionUrl() + suffix) }
        sessionToken = null
    }

    private fun parseSession(row: JSONObject): UserSession {
        val user = row.getJSONObject("user")
        return UserSession(
            token = row.optString("token", sessionToken.orEmpty()),
            expiresAt = row.optString("expiresAt", ""),
            user = UserAccount(
                id = user.optInt("id"),
                name = user.optString("name", "Usuario"),
                email = user.optString("email", sessionEmail),
                role = user.optString("role", "consulta")
            )
        ).also {
            if (it.token.isNotBlank()) sessionToken = it.token
        }
    }

    private fun request(method: String, endpoint: String, body: String? = null): String {
        val token = sessionToken ?: throw IllegalStateException("Sesion requerida. Inicia sesion.")
        return try {
            requestRaw(method, endpoint, body, token)
        } catch (error: IllegalStateException) {
            if (error.message?.contains("HTTP 401") == true || error.message?.contains("Sesion", ignoreCase = true) == true) {
                sessionToken = null
                throw IllegalStateException("Sesion expirada. Inicia sesion nuevamente.")
            } else {
                throw error
            }
        }
    }

    private fun requestRaw(method: String, endpoint: String, body: String? = null, token: String? = sessionToken): String {
        val connection = (URL(endpoint).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 8000
            readTimeout = 8000
            setRequestProperty("Accept", "application/json")
            if (!token.isNullOrBlank()) {
                setRequestProperty("Authorization", "Bearer $token")
            }
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
        }

        try {
            if (body != null) {
                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { it.write(body) }
            }

            val status = connection.responseCode
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            val response = stream?.bufferedReader()?.use(BufferedReader::readText).orEmpty()

            if (status !in 200..299) {
                val message = runCatching { JSONObject(response).optString("error") }.getOrNull()
                throw IllegalStateException(message?.takeIf { it.isNotBlank() } ?: "HTTP $status")
            }

            return response
        } finally {
            connection.disconnect()
        }
    }
}

