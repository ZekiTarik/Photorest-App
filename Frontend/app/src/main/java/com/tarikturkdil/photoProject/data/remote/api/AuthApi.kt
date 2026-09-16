package com.tarikturkdil.photoProject.data.remote.api

import com.tarikturkdil.photoProject.data.remote.dto.AuthResponse
import com.tarikturkdil.photoProject.data.remote.dto.LoginRequest
import com.tarikturkdil.photoProject.data.remote.dto.RefreshTokenRequest
import com.tarikturkdil.photoProject.data.remote.dto.RegisterRequest
import com.tarikturkdil.photoProject.data.remote.dto.UserResponse
import com.tarikturkdil.photoProject.data.remote.dto.common.RootEntity
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): RootEntity<UserResponse>

    @POST("/authenticate")
    suspend fun login(@Body request: LoginRequest): RootEntity<AuthResponse>

    @POST("/refreshToken")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RootEntity<AuthResponse>

    @POST("/logout")
    suspend fun logout(@Body request: RefreshTokenRequest): RootEntity<Unit>
}