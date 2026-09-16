package com.tarikturkdil.photoProject.domain.repository

import com.tarikturkdil.photoProject.domain.model.User

interface AuthRepository {
    suspend fun register(username: String, email: String, password: String): AppResult<User>
    suspend fun login(email: String, password: String): AppResult<Unit>
    suspend fun logout(): AppResult<Unit>
    suspend fun isLoggedIn(): Boolean
}