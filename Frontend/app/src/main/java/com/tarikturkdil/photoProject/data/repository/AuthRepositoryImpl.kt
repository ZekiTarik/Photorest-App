package com.tarikturkdil.photoProject.data.repository

import com.tarikturkdil.photoProject.data.local.TokenDataStore
import com.tarikturkdil.photoProject.data.remote.api.AuthApi
import com.tarikturkdil.photoProject.data.remote.dto.LoginRequest
import com.tarikturkdil.photoProject.data.remote.dto.RefreshTokenRequest
import com.tarikturkdil.photoProject.data.remote.dto.RegisterRequest
import com.tarikturkdil.photoProject.domain.model.User
import com.tarikturkdil.photoProject.domain.repository.AppResult
import com.tarikturkdil.photoProject.domain.repository.AuthRepository
import kotlinx.serialization.json.Json
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import com.tarikturkdil.photoProject.data.remote.dto.common.ApiError

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val tokenDataStore: TokenDataStore,
    private val json: Json
) : AuthRepository {

    override suspend fun register(
        username: String,
        email: String,
        password: String
    ): AppResult<User> {
        return try {
            val response = authApi.register(RegisterRequest(username, email, password))
            val userResponse = response.payload
                ?: return AppResult.Error("Sunucudan beklenmeyen yanıt alındı.")

            AppResult.Success(
                User(
                    id = userResponse.id ?: -1L,
                    username = userResponse.username,
                    avatarUrl = userResponse.avatarUrl,
                    bio = userResponse.bio
                )
            )
        } catch (e: HttpException) {
            AppResult.Error(parseErrorMessage(e))
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor. İnternet bağlantınızı kontrol edin.")
        }
    }

    override suspend fun login(email: String, password: String): AppResult<Unit> {
        return try {
            val response = authApi.login(LoginRequest(email, password))
            val authResponse = response.payload
                ?: return AppResult.Error("Sunucudan beklenmeyen yanıt alındı.")

            tokenDataStore.saveTokens(authResponse.token, authResponse.refreshToken)
            AppResult.Success(Unit)
        } catch (e: HttpException) {
            AppResult.Error(parseErrorMessage(e))
        } catch (e: IOException) {
            AppResult.Error("Sunucuya ulaşılamıyor. İnternet bağlantınızı kontrol edin.")
        }
    }

    override suspend fun logout(): AppResult<Unit> {
        return try {
            val refreshToken = tokenDataStore.getRefreshToken()
            if (refreshToken != null) {
                authApi.logout(RefreshTokenRequest(refreshToken))
            }
            tokenDataStore.clearTokens()
            AppResult.Success(Unit)
        } catch (e: Exception) {
            tokenDataStore.clearTokens()
            AppResult.Success(Unit)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenDataStore.getAccessToken() != null
    }

    private fun parseErrorMessage(e: HttpException): String {
        val errorBody: ResponseBody? = e.response()?.errorBody()
        return try {
            val errorText = errorBody?.string()
            val apiError = json.decodeFromString<ApiError>(errorText ?: "")
            apiError.exception?.message ?: "Bilinmeyen bir hata oluştu."
        } catch (parseException: Exception) {
            "Bilinmeyen bir hata oluştu (kod: ${e.code()})."
        }
    }
}