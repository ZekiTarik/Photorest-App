package com.tarikturkdil.photoProject.data.remote

import com.tarikturkdil.photoProject.data.local.TokenDataStore
import com.tarikturkdil.photoProject.data.remote.api.AuthApi
import com.tarikturkdil.photoProject.data.remote.dto.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class TokenAuthenticator @Inject constructor(
    private val tokenDataStore: TokenDataStore,
    private val authApiProvider: Provider<AuthApi>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Zaten bir kere retry denendiyse, tekrar deneme (sonsuz döngü riski)
        if (responseCount(response) >= 2) return null

        val refreshToken = runBlocking { tokenDataStore.getRefreshToken() }
            ?: return null // refresh token yoksa yapacak bir şey yok, login'e düşecek

        return runBlocking {
            try {
                val result = authApiProvider.get().refreshToken(
                    RefreshTokenRequest(refreshToken)
                )
                val newAuth = result.payload ?: return@runBlocking null

                tokenDataStore.saveTokens(newAuth.token, newAuth.refreshToken)

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${newAuth.token}")
                    .build()
            } catch (e: Exception) {
                null // refresh de başarısız oldu, kullanıcı yeniden login olmalı
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}