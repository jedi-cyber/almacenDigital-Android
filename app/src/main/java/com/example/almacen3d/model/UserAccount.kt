package com.example.almacen3d.model

data class UserAccount(
    val id: Int,
    val name: String,
    val email: String,
    val role: String
)

data class UserSession(
    val token: String,
    val expiresAt: String,
    val user: UserAccount
)

data class ActiveSession(
    val id: Int,
    val current: Boolean,
    val ipAddress: String,
    val userAgent: String,
    val lastSeenAt: String,
    val expiresAt: String
)
