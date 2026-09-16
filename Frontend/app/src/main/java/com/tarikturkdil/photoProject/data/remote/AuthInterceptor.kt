package com.tarikturkdil.photoProject.data.remote

import com.tarikturkdil.photoProject.data.local.TokenDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Login/Register/Refresh endpoint'lerine token eklemeye gerek yok
        val noAuthPaths = listOf("/authenticate", "/register", "/refreshToken")
        if (noAuthPaths.any { originalRequest.url.encodedPath.contains(it) }) {
            return chain.proceed(originalRequest)
        }

        val accessToken = runBlocking { tokenDataStore.getAccessToken() }

        val newRequest = if (accessToken != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}